package search;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.fastinfoset.algorithm.IEEE754FloatingPointEncodingAlgorithm;

public class Packages {
  public String name;
  public int classlines=0;
  List<Classes> pkClasses=new ArrayList<Classes>();
  public Packages(String name) {
	  this.name=name;
  }
}
