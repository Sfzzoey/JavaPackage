package search;

import java.io.IOException;

import javax.management.ListenerNotFoundException;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister.Pack;

public class Relation {
   String  rname;
   String lnote;
   String rnote;
  public Relation(String lnote,String rnote) {
	this.lnote=lnote;
	this.rnote=rnote;
  }
  public boolean equals(Object obj) {
	  return this.lnote.equals(lnote)&&this.rnote.equals(rnote);
  }
  public int hashCode()//ÖØÐ´  
	{
		return lnote.hashCode()+rnote.hashCode() ;
	}
  public String toString()
 {
	return lnote+" and "+rnote+"  ";  
 }
	 }

