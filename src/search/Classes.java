package search;

import java.util.ArrayList;
import java.util.List;

public class Classes {
  public String name;
  public String packname;
  public boolean trueclass=true;
  public List<String> method=new ArrayList<String>();
  public int lines;
  
  public Classes (String classname,String packname)
  {
	  this.name=classname;
	  this.packname=packname;
	  
  }
}