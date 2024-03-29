package search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.css.ElementCSSInlineStyle;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.StackInstruction;
import com.sun.org.glassfish.external.statistics.annotations.Reset;

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
	  // pkLines();
	  // pkclass();
	   //findMethod();
    	//superClass();
//   
    	CountRelation();
        //relationShip();
//    try {	
//     for(Class<?> clazz:classObject(rootPath)) {
//     System.out.println("类："+clazz.getName()+"方法的参数们：");   	
//    	for(String[] infos: getMethodInfo(clazz)) {
//    		for(int i=0;i<infos.length;i++) {
//    		 if(i==0)
//    			{System.out.println(infos[i]);}
//    			else {
//    				System.out.print(infos[i]+" , ") ;	
//				}
//    		}
//    	}
//    	}
//    }catch (Exception e) {
//		
//	}
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
    
    public static Set<Relation> relationShip() throws IOException {
    	String pkname=null;
    	Set<Relation> relationset=new HashSet<Relation>();
    	List<Class<?>> clslist=classObject(rootPath);
    	
	    for(Class<?>cls:clslist) {
		  //继承基类
	    	Class<?> supclass=cls.getSuperclass();
		 
			if(supclass!=null)	
			{ 
			 
			 pkname=supclass.getPackage().getName();
			   if(!pkname.equals(cls.getPackage().getName()) && pkname.indexOf("java.")==-1)
			   {   
				  // System.out.println(cls.getName()+"类的基类为："+supclass.getName());
				  // System.out.println("包"+cls.getPackage().getName()+"依赖于包"+pkname);
			     relationset.add(new Relation(cls.getPackage().getName(),pkname));
			   }else {
				  // System.out.println("包内依赖");
			   }
			     
			}//else 
				//System.out.println("该类中没有基类");
	//实现的接口		
			Class<?>[] interlist=cls.getInterfaces();
		for(Class<?> itfc:interlist)
		{
			String intername=itfc.getName();
			if(itfc.getPackage()!=cls.getPackage()&&intername.indexOf("java.")==-1)
			{
				relationset.add(new Relation(cls.getPackage().getName(),itfc.getPackage().getName()));
			}
			//System.out.println(intername);
		}
		// 成员变量依赖		
		//System.out.println("类"+cls.getName()+"中字段的名字");
		Field[] fs= cls.getDeclaredFields(); 
		String lpkname=cls.getPackage().getName();
        String lclazz=cls.getName();
               
	    for(Field f:fs) {
	         
	    	 String typename=f.getType().getName(); //返回的类型的全名
	    	 if(typename.lastIndexOf(".")!=-1)
	    	 {
	    	   String rpkname=typename.substring(0,typename.lastIndexOf("."));
	    	    	    		    	 			  	    		    	    	  
	    	   if(!lpkname.equals(rpkname)&& rpkname.indexOf("java.")==-1)
	    		{   
	    		  //System.out.println(rpkname);
	    			relationset.add(new Relation(lpkname,rpkname));	
	    		}}}
	    /*成员函数的参数，抛出异常，返回值的类型*/
		    
	 try {   for(String[] infos: getMethodInfo(cls)) {
		 
		 
    		for(String info:infos) {
    		  if(info.lastIndexOf(".")!=-1) {
    			 String mthInfoPkname=info.substring(0,info.lastIndexOf("."));	 
    			 if(!lpkname.equals(mthInfoPkname)&& mthInfoPkname.indexOf("java.")==-1)
 	    		{   
 	    		  
 	    			relationset.add(new Relation(lpkname,mthInfoPkname));	
 	    		}
    		 }
    		}
    	}}catch (Exception e) {
			
		}
	    
	    }
	    //System.out.println(relationset);
	    
	    return relationset;
    } 
    
    public static void CountRelation()throws IOException{
    	Set<Relation> relations =relationShip();
    	Packages[] pks =findPackage();
    	int ce=0;
    	for(Packages pk:pks) {
    		System.out.print("包"+pk.name+"的Ce为：");
    		for(Relation relation:relations)
    		 {
    			if(pk.name.equals(relation.lnote)) {
    			ce++;}
    	     }
    		System.out.println(ce);
    		ce=0;
    	}
    }
    
    
    public static Packages[] findPackage() throws IOException {	
	
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
		   for(Class<?> clazz:classObject(rootPath))
		   {
			   if(clazz.getPackage().getName().equals(pk.name))
			   {
				   pk.pkClazz.add(clazz);
			   }
		   }
	   }
	  return packagee;
	}
    
    public static Classes[] classInfor(List<Class<?>> classeslist,String rootPath) throws IOException {   	    	 
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
	  File file=new File("D:\\packbugpredict\\projectsource\\jaxen-master\\jaxen-master"); 
	  int plines=0;   
	  for(Packages pk:findPackage())
	  {  
		  
		 for(Class<?> cls: pk.pkClazz)
		 {
			 List<Integer> clines=getClasslines(file, cls.getName().replace(".","\\"),new ArrayList<Integer>());   	      
		       if(clines.size()>0)
		        { 
		    	   plines=plines+clines.get(0);    	       
		    	   
		    	}
			// System.out.println(cls.name);
			 
	 }		
		System.out.println(pk.name+"类的总行数为："+plines);	
		  plines=0;  
	  }
	 
   }
   public static void pkclass()throws IOException{
		 // Packages[] pk= findPackage(rootPath);	
	      float uclazz=0;
	      float clazz=0;
	      float ucratio=0;
		 
	      for(Packages pk:findPackage())
		  { 
			  
			 for(Class<?> cls: pk.pkClazz)
			 {  
				 ++clazz;
				if(cls.isInterface()==true||Modifier.isAbstract(cls.getModifiers())==true)
				  {
					++uclazz;
				  }
			       
				// System.out.println(cls.name);
		    }
			   ucratio=uclazz/clazz;
			   System.out.println(pk.name+"包中接口或抽象类比率："+ucratio);			  
			   uclazz=0;
		       clazz=0;
		       ucratio=0;
		  }
		 
	   }
   public static void findMethod() throws IOException{
	   StringBuffer buffer = new StringBuffer();
	   List<String> methodlist=new ArrayList<String>();
	   for(Packages pk:findPackage()) {
		   for(Class<?> cls: pk.pkClazz)
		   { Method[] methods= cls.getDeclaredMethods();
		     System.out.println(cls.getName()+"类的声明的方法有：");
		     for (int i = 0; i < methods.length; i++) {
	    			String mothodModifer = Modifier.toString(methods[i].getModifiers());//方法的修饰词
	    			buffer.append(mothodModifer + " ");
	     
	    			Class mothodReturnType = methods[i].getReturnType();//返回值类型
	    			String simpleName = mothodReturnType.getSimpleName();		     			
	    			buffer.append(simpleName + " ");
	     
	    			String mothodName = methods[i].getName();
	    			buffer.append(mothodName + " (");
	     
	    			Class[] parameterTypes = methods[i].getParameterTypes();
	    			Parameter[] parameters = methods[i].getParameters();
	    			for (int j = 0; j < parameterTypes.length; j++) {
	    				String simpleName2 = parameterTypes[j].getSimpleName();
	    				String name = parameters[j].getName();
	    				if (j == 0) {
	    					buffer.append(simpleName2 + " " + name);
	    				} else {
	    					buffer.append("," + simpleName2 + " " + name);
	    				}
	    			}
	    			buffer.append("){" + System.getProperty("line.separator"));
	     
	    			buffer.append("}" + System.getProperty("line.separator"));
	     
	    			methodlist.add(buffer.toString());
	    			
	    			//System.out.println(buffer3.toString());
	    			buffer.delete(0, buffer.length());
	    			
	    		}
		    for(String mts:methodlist)
		     {System.out.println(mts);}
		    methodlist.clear();
		     
	       }
	 }
   }
   
   public static  List<String[]> getMethodInfo(Class<?> cls) throws Exception{
	   Method[] methods= cls.getDeclaredMethods();
	   StringBuffer buffer = new StringBuffer(); 
	   List<String[]> methodlist=new ArrayList<String[]>();
	   // System.out.println(cls.getName()+"类的声明的方法有：");
	   try {  
	   for (int i = 0; i < methods.length; i++) {
	    	
	    	//String mothodName = methods[i].getName();
	    	//buffer.append("方法："+mothodName+"的信息为:,");
	    	Class mothodReturnType = methods[i].getReturnType();//返回值类型			
	    	String simpleName = mothodReturnType.getName(); 
 			buffer.append(simpleName+",");
 			
 			Class[] parameterTypes = methods[i].getParameterTypes();
 			if(parameterTypes.length!=0)
 			  {for (int j = 0; j < parameterTypes.length; j++) {
 				String paraTypeName=parameterTypes[j].getName();
 				buffer.append(paraTypeName+",");
 			  } }
 			Class[] exceptions=methods[i].getExceptionTypes();
 			if(exceptions.length!=0)
 			 {for(int j=0;j<exceptions.length;j++) {
 				String expectionTypeName=exceptions[j].getName();
 				if(j==exceptions.length-1)
 					buffer.append(expectionTypeName);
 				else {
 					buffer.append(expectionTypeName+",");
 				} }
 			}
 			 methodlist.add(buffer.toString().split(","));
 			 buffer.delete(0, buffer.length());
	     }
	   } catch (Exception e) {
           e.printStackTrace();
       }
	    
	     return methodlist;
   }
 }