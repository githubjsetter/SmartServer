package com.inca.tools.comparadir;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

public class Dirfileinfo {
	HashMap<String, String> filemd5map=new HashMap<String, String>();
	File dir;
	Dirfileinfo parentnode;
	Vector<Dirfileinfo> children=new Vector<Dirfileinfo>();
}
