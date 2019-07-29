package search;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageClass {
   public static void main(String[] args)throws IOException {
	   String rootPath="/D:/packbugpredict/jaxen-1.1.6/"; //针对 java项目		
	   File rootFile = new File(rootPath);
	   Classes[] clazz= classInfor(rootPath);
	   for(int i=0;i<clazz.length;i++)
	   {System.out.println(clazz[i]);}
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
    
    
    
    public static void findPackage(String rootPath) throws IOException {	
	
	   Set<String> packfileset = new HashSet<String>();
	
	   for(Class<?> classeslist:classObject(rootPath))
	     { 
		
		   packfileset.add(classeslist.getPackage().getName());
				 
	     } 
	//System.out.println(packfileset);
	 
	}
    
    public static Classes[] classInfor(String rootPath) throws IOException {
    	List<Class<?>> classeslist=new ArrayList<Class<?>>();
   	    classeslist=classObject(rootPath);
   	    Classes[] clazz=new Classes [classeslist.size()];
   	    for(int i=0;i< classeslist.size();i++) {
   	       String	cname=classeslist.get(i).getName();
   	       String  pname=classeslist.get(i).getPackage().getName();
   	      if(classeslist.get(i).isInterface()==true)	
   	        clazz[i]=new Classes(cname,pname,false);
   	      else
   	    	clazz[i]=new Classes(cname,pname,true);
   	    }
   	     return clazz;
    }
 }