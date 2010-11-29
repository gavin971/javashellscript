//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8

/**
 * Copyright (c) 2010, Robert Schuster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * - Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * - Neither the name of Robert Schuster nor the names of his
 *   contributors may be used to endorse or promote products
 *   derived from this software without specific prior written
 *   permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import jsslib.shell.ArgParser;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Structure;
import ucar.nc2.Variable;


/**
 * This Example will create a netcdf file from a hdf5-file with the same fields
 * but with a flatt namespace
 *
 * @author robert schuster
 */
public class hdf5flatten {

    private static ArrayList<Dimension> destdims = null;
    private static boolean removeGroupName = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //have a look at the arguments
        Properties arguments = ArgParser.ArgsToProperties(args);

        //show the discription and exit on no arguments
        if (arguments == null || arguments.size() < 2) {
            ShowDiscription();
            return;
        }

        //The first argument is the source file
        String sourcefilename = arguments.getProperty("unnamed0");
        File sourcefile = new File(sourcefilename);
        if (!sourcefile.isFile()) {
            System.out.println("ERROR: "+sourcefilename + " is not a file!");
            return;
        }

        //Delete the dest-file if it is already there
        boolean removeDest = false;
        if (arguments.containsKey("force")) {
            removeDest = true;
        }


        //The first argument is the source file
        String destfilename = arguments.getProperty("unnamed1");
        File destfile = new File(destfilename);
        if (destfile.isFile()) {
            if (removeDest == false) {
                System.out.println("ERROR: "+destfilename + " already exists!");
                return;
            } else {
                destfile.delete();
            }
        }

        //remove the groupname
        if (arguments.containsKey("rg")) {
            removeGroupName = true;
            System.out.println("Removing the Groupname from all Variables and Dimension!");
        }

        //open the sourcefile
        NetcdfFile source = null;
        NetcdfFileWriteable dest = null;
        try {
            //open the source file
            source = NetcdfFile.open(sourcefilename);

            //open the destination file
            dest = NetcdfFileWriteable.createNew(destfilename);
            List<Variable> vars = source.getVariables();
            List<Dimension> dims = source.getDimensions();
            destdims = new ArrayList<Dimension>();
            //Dimensions
            System.out.println("Dimensions:");
            int unnamed = 0;
            for (Variable var:vars) {
                List<Dimension> vardims = var.getDimensions();
                for (Dimension dim:vardims) {
                    if (dim.getName() == null) {
                        dim.setName("unnamed"+unnamed);
                        unnamed++;
                    }
                    if (dim.getGroup() == null) {
                        dim.setGroup(source.getRootGroup());
                    }
                    if (!dims.contains(dim)) dims.add(dim);
                }
                if (var.getDataType() == DataType.STRUCTURE) {
                    Structure struct = (Structure)var;
                    List<Variable> svars = struct.getVariables();
                    for (Variable svar:svars) {
                        List<Dimension> svardims = svar.getDimensions();
                        for (Dimension dim:svardims) {
                            if (dim.getName() == null) {
                                dim.setName("unnamed"+unnamed);
                                unnamed++;
                            }
                            if (dim.getGroup() == null) {
                                dim.setGroup(source.getRootGroup());
                            }
                            if (!dims.contains(dim)) dims.add(dim);
                        }
                    }
                }
            }
            //check if there are duplicate flat names
            ArrayList<String> flatnames = new ArrayList<String>();
            for (Dimension dim:dims) {
                String flatname = Name2FlatName(dim.getName());
                if (!flatnames.contains(flatname)) {
                    flatnames.add(flatname);
                    System.out.println(dim.toString());
                    destdims.add(dest.addDimension(flatname, dim.getLength()));
                }
            }
            //Variables
            for (Variable var:vars) {
                String flatname = Name2FlatName(var.getName());
                System.out.println("Define: " + var.getName() + " -> " + flatname);
                if (var.getDataType() != DataType.STRUCTURE) {
                    Variable temp = dest.addVariable(flatname, var.getDataType(), getFlatDims(var));
                    List<Attribute> atts = var.getAttributes();
                    for (Attribute att:atts) dest.addVariableAttribute(temp, att);
                } else {
                    Structure struct = (Structure)var;
                    List<Variable> svars = struct.getVariables();
                    for (Variable svar:svars) {
                        System.out.println("Define: " + var.getName() + "." + svar.getShortName() + " -> " + flatname+"_"+svar.getShortName());
                        Variable temp = dest.addVariable(flatname+"_"+svar.getShortName(), svar.getDataType(), getFlatDims(svar));
                        List<Attribute> atts = svar.getAttributes();
                        for (Attribute att:atts) dest.addVariableAttribute(temp, att);
                    }
                }
            }
            //Global attributes
            List<Attribute> gatts = source.getGlobalAttributes();
            for (Attribute att:gatts) {
                Attribute natt = new Attribute(Name2FlatName(att.getName()), att);
                dest.addGlobalAttribute(natt);
            }
            //create the destfile
            dest.create();
            //write the data to the dest-file
            for (Variable var:vars) {
                String flatname = Name2FlatName(var.getName());
                try {
                    if (var.getDataType() != DataType.STRUCTURE) {
                        System.out.println("Write: " + var.getName() + " -> " + flatname);
                        dest.write(flatname, var.read());
                    } else {
                        Structure struct = (Structure)var;
                        List<Variable> svars = struct.getVariables();
                        for (Variable svar:svars) {
                            System.out.println("Write: " + var.getName() + "." + svar.getShortName() + " -> " + flatname+"_"+svar.getShortName());
                            dest.write(flatname+"_"+svar.getShortName(), svar.read());
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("        -> unable to write data:");
                    System.out.println("        -> " + ex.toString());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (source != null) source.close();
                if (dest != null) dest.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private static void ShowDiscription() {
        System.out.println();
        System.out.println("This Example will create a netcdf file from a hdf5-file with the same fields");
        System.out.println("but with a flatt namespace");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("hdf5flatten input.hdf output.nc");
        System.out.println("");
        System.out.println("Parameters:");
        System.out.println("    -rg     : remove Groupnames");
        System.out.println("    -force  : overwrite destination file");
    }

    /**
     * convert a variable name to a flat nc3 name
     * @param name
     * @return
     */
    private static String Name2FlatName(String name) {
        String result = null;
        if (removeGroupName) {
            int si = name.lastIndexOf("/");
            if (si != -1) result = name.substring(si+1);
            else result = name;
        }
        result = result.replaceAll("[/| |-|.]", "_");
        return result;
    }

    /**
     * find the new flat dims that fit to the source dims
     * @param sourcevar
     * @return
     * @throws Exception
     */
    private static Dimension[] getFlatDims(Variable sourcevar) throws Exception {
        List<Dimension> sourcedims = sourcevar.getDimensions();
        Dimension[] result = new Dimension[sourcedims.size()];
        for (int i= 0;i<result.length;i++) {
            String flatname = Name2FlatName(sourcedims.get(i).getGroup().getName()+"/"+sourcedims.get(i).getName());
            int dimindex = -1;
            for (int j=0;j<destdims.size();j++) {
                if (destdims.get(j).getName().equals(flatname)) {
                    dimindex = j;
                    break;
                }
            }
            if (dimindex != -1) result[i] = destdims.get(dimindex);
            else {
                throw new Exception("Dimension not found: " + flatname);
            }
        }
        return result;
    }
}
