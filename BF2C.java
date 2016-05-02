/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 condorcraft110
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.*;
import java.util.*;
import java.nio.charset.*;

public class BF2C
{
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.err.println("no input file specified");
			return;
		}
		
		File inFile = new File(args[0]);
		
		if(!inFile.exists())
		{
			System.err.println("input file not found");
			return;
		}
		
		if(inFile.isDirectory())
		{
			System.out.println("input file is a directory");
			return;
		}
		
		if(inFile.length() == 0L)
		{
			System.out.println("input file is empty");
		}
		
		try
		{
			StringBuilder builder = new StringBuilder();
			
			builder.append("#include <stdio.h>\n\nint main()\n{\n\tchar array[32768] = {0};\n\tchar *ptr = array;\n\t\n");
			
			String indent = "\t";
			
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(inFile));
			int read;
			boolean requiresNewline = false;
			
_L1:		while((read = in.read()) != -1)
			{
				if(requiresNewline) builder.append(indent + "\n");
				
				switch((char)read)
				{
					case '>':
						builder.append(indent + "ptr++;\n");
						break;
					case '<':
						builder.append(indent + "ptr--;\n");
						break;
					case '+':
						builder.append(indent + "(*ptr)++;\n");
						break;
					case '-':
						builder.append(indent + "(*ptr)--;\n");
						break;
					case '.':
						builder.append(indent + "putchar(*ptr);\n");
						break;
					case ',':
						builder.append(indent + "*ptr = getchar();\n");
						break;
					case '[':
						if(!requiresNewline) builder.append(indent + "\n");
						builder.append(indent + "while(*ptr)\n" + indent + "{\n");
						indent += "\t";
						break;
					case ']':
						indent = indent.substring(0, indent.length() - 1);
						builder.append(indent + "}\n");
						requiresNewline = true;
						continue _L1;
				}
				
				requiresNewline = false;
			}
			
			in.close();
			
			builder.append("\t\n\treturn 0;\n}\n\n");
			
			File outFile = new File(args.length > 1 ? args[1] : args[2] + ".c");
			
			if(outFile.exists())
			{
				if(outFile.isDirectory())
				{
					System.err.println("output file already exists and is a directory");
					return;
				}
				else
				{
					System.out.print("output file already exists. overwrite (y/n)? ");
					
					Scanner stdin = new Scanner(System.in);
					
					String line = stdin.nextLine();
					
					stdin.close();
					
					if(line.trim().equalsIgnoreCase("y"))
					{
						System.out.println("overwriting");
						outFile.delete();
					}
					else
					{
						System.err.println("aborting");
						return;
					}
				}
			}
			
			FileOutputStream out = new FileOutputStream(outFile);
			
			out.write(builder.toString().getBytes(StandardCharsets.UTF_8));
			
			out.close();
			
			System.out.println("done");
		}
		catch(FileNotFoundException e)
		{
			System.err.println("input file not found");
			return;
		}
		catch(IOException e)
		{
			System.err.println("IO exception: " + e.getMessage());
		}
	}
}
