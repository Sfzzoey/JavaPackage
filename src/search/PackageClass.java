package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageClass {
	
	public static String rootPath="/D:/packbugpredict/jaxen-2.0.0-BETA/"; //针对 java项目		
	public static File rootFile = new File(rootPath);
    
  public static void main(String[] args)throws IOException {	 
//	   Classes[] clazz= classInfor(rootPath);
//	   for(int i=0;i<clazz.length;i++)
//	   {   
//		   if(clazz[i].lines>0)
//		      System.out.println(clazz[i].lines);
//		   else
//			  System.out.println("找不到该"+clazz[i].name+".class文件对应的.java文件的路径"); 
//	   }
	  pkLines();
//	  for(Packages pk:findPackage(rootPath))
//	  {  
//		  System.out.println(pk.name);
//		
//	
//		// System.out.println(pk[i].name+"类的总行数为："+plines);	
//	  }
  }


  public static List<String> listclassFiles(File rootFile,List<String> fileList,String rootPath ) throws IOException{
		//该方法通过包的绝对路径去遍历文件路径中的.class文件，找到类的全名（包名+类名）
			File[] allFiles = rootFile.listFiles();
			String crootPath=rootPath;
			for(File file : allFiles){
				if(file.isDirectory()){
					listclassFiles(file, fileList,rootPath);
				}
				if(file.getName().endsWith(".class"))
				{
					String path = file.getCanonicalPath(); //输出标准的绝对路径				
				    String cname=path.substring(rootPath.length()-1,path.length());
				    String cnameAll=cname.replace("//", ".").substring(0,cname.lastIndexOf(".")).replace("\\", ".");
				//cnameAll 为找到的类全名，格式为；com.gather.SearchNum.App
				//转成这个格式是因为ucl.loadClass(classNameAll);里面需要输入类的全名			
				//System.out.println(cnameAll);
					fileList.add(cnameAll);
					//fileList.add(clazz.replace("//", ".").substring(0,clazz.lastIndexOf(".")));
				}
			}
			return fileList;
		}
 
  
 public  static List<Class<?>> classObject(String rootPath) throws IOException{ 
    	Class<?> cls=null;
    	List<Class<?>> classlist=new ArrayList<Class<?>>();
    	try {  
	      	  File file=new File(rootPath); //path 为包的绝对路径，不含包名
	    	   URL url=file.toURL();
	           URLClassLoader ucl = new URLClassLoader(new URL[]{url});
	           // 上面三个语句可以让虚拟机加载磁盘路径中的文件，进而在里面寻找class文件
	           for(String classfilename:listclassFiles(file, new ArrayList<String>(),rootPath))   
	             { cls = ucl.loadClass(classfilename);
	              classlist.add(cls);
                 }
	           
		    }
    	
		catch (ClassNotFoundException e) {  
          e.printStackTrace();  
      } 
    	return classlist;
   } 
    
    
    
    public static Packages[] findPackage(String rootPath) throws IOException {	
	
	   Set<String> packfileset = new HashSet<String>();
	   
	   
	   for(Class<?> classeslist:classObject(rootPath))
	     { 
		
		   packfileset.add(classeslist.getPackage().getName());
		   
				 
	     } 
	//System.out.println(packfileset);
	   Packages[] packagee=new Packages[packfileset.size()];
	   int i=0;
	   for(String plistname:packfileset)
	   {
		   packagee[i++]=new Packages(plistname);
		 
	   }
	   for(Packages pk:packagee) {
		   for(Classes cls:classInfor(rootPath))
		   {
			   if(cls.packname.equals(pk.name))
			   {
				   pk.pkClasses.add(cls);
			   }
		   }
	   }
	  return packagee;
	}
    
    public static Classes[] classInfor(String rootPath) throws IOException {
    	File file=new File("D:\\packbugpredict\\projectsource\\jaxen-master\\jaxen-master");
    	List<Class<?>> classeslist=new ArrayList<Class<?>>();
   	    classeslist=classObject(rootPath);   	    
   	    Classes[] clazz=new Classes [classeslist.size()];
   	    for(int i=0;i< classeslist.size();i++) {   	    	
   	       String  cname=classeslist.get(i).getName();  	       
   	       String  pname=classeslist.get(i).getPackage().getName();
   	       clazz[i]=new Classes(cname,pname);
   	      if(classeslist.get(i).isInterface()==true)	
   	        {    
 	            
   	             clazz[i].trueclass=false;  	
   	        }
   	      if(Modifier.isAbstract(classeslist.get(i).getModifiers())==true )
   	        {
   	    	   clazz[i].trueclass=false;
   	        }
   	      
   	  //  lines= getClassName(file,classeslist.get(i).getName());
   	       List<Integer> clines=getClasslines(file, cname.replace(".","\\"),new ArrayList<Integer>());   	      
	       if(clines.size()>0)
	        { 
	    	   clazz[i].lines=clines.get(0);   	       
	    	   
	    	}
//	       else {
//	    	 System.out.println("找不到该"+cname+".class文件对应的.java文件的路径");
//	       }
   	    }
   	     return clazz;
    }
   public static List<Integer> getClasslines(File file,String clsname,List<Integer> clineslist){		
	  String cname=clsname;	
	  File[] listFiles = file.listFiles();
	 
	
		if(listFiles!=null && listFiles.length>0){
			
			for(File file2: listFiles){
				
				if(file2.isDirectory()){
					getClasslines(new File(file2.getAbsolutePath()),cname,clineslist);
				}
				
				if(file2.isFile()){
					
					String path = file2.getAbsolutePath();
					
					if(path.endsWith(".java")&& path.indexOf(cname+".java")>=0 ){
						//cloclist.add(file2.getAbsolutePath()) ;
						try {
						    FileReader fileReader = new FileReader(file2);
						    LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
  					        lineNumberReader.skip(Long.MAX_VALUE);
						    clineslist.add(lineNumberReader.getLineNumber() + 1) ;
						    fileReader.close();
						    lineNumberReader.close();
						   // System.out.println(lines); 
						   // break;
						} catch (IOException e) {
						    e.printStackTrace();
						}
						  
					}
										
				}
			
			}
			
		}
		
		
		 return  clineslist;
	}
   
   public static void pkLines()throws IOException{
	 // Packages[] pk= findPackage(rootPath);
	  
	  int plines=0;   
	  for(Packages pk:findPackage(rootPath))
	  {  
		  
		 for(Classes cls: pk.pkClasses)
		 {
			// System.out.println(cls.name);
			 plines=plines+cls.lines;
	 }
		System.out.println(pk.name+"类的总行数为："+plines);	
	  }
	 
   }
    
 }