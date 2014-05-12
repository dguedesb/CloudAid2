package searchEngine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;






import exceptions.ErrorEnum;
import exceptions.ReadModelException;


public class TripleStore {
	

	private Model myTripleStore;
	private Map<String,String> myPrefixes;
	public TripleStore() throws IOException, ReadModelException
	{
		setMyTripleStore(this.importModel("./ProviderSets/"));
	}
	
	private Model importModel(String path) throws IOException, ReadModelException{
		Model model = ModelFactory.createDefaultModel();
//		this.setPrefixes(model);
		
		ArrayList<String> fileNames = new ArrayList<String>();
		Map<String, String> prefixes = new HashMap<String, String>(); //inverted map of prefixes KEY = URI, VALUE = name
		
		fileNames = this.getFileNames(path);
		System.out.println(fileNames);
		
		for(String file : fileNames){
			System.out.println("FILE: "+file);
			Model temp;
			//System.out.println(test.getFileExtension(file));
			String ext = this.getFileExtension(file);
			if(ext.equalsIgnoreCase("ttl")){
				temp = this.readFile(file, "TTL" );
				if(temp != null){
					prefixes = this.processPrefixes(temp, prefixes);
//					this.addPrefix(temp, model);
					model.add(temp);
				}
			}else if(ext.equalsIgnoreCase("rdf")){
				temp = this.readFile(file, "RDF/XML" );
				if(temp != null){
					prefixes = this.processPrefixes(temp, prefixes);
//					this.addPrefix(temp, model);
					model.add(temp);
				}
			}
		}
		this.setMyPrefixes(prefixes);
		model = this.setPrefixes(model, prefixes);
		
		return model;
	}
	
	private ArrayList<String> getFileNames(String path) throws IOException{
		final ArrayList<String> fileNames = new ArrayList<String>();
		
	    Path startPath = Paths.get(path);
	    Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
//	            System.out.println("Dir: " + dir.toString());
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
//	            System.out.println("File: " + file.toString());
	            if (file.getFileName().toString().endsWith(".ttl") || file.getFileName().toString().endsWith(".rdf")){
	            	fileNames.add(file.toString());
	            }		            
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult visitFileFailed(Path file, IOException e) {
	            return FileVisitResult.CONTINUE;
	        }
	    });
		
		return fileNames;
	}
	
	private String getFileExtension(String file){
		String ext = null;
	    int i = file.lastIndexOf('.');

	    if (i > 0 &&  i < file.length() - 1) {
	        ext = file.substring(i+1).toLowerCase();
	    }
	    return ext;
	}
	
	private Map<String, String> processPrefixes(Model model, Map<String, String> prefixes) throws ReadModelException{
		Map<String, String> result = prefixes;
		
		
		try {
			String name = this.getModelName(model);
			Iterator<Entry<String,String>> it = model.getNsPrefixMap().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
		        String key = (String)pairs.getKey();
		        if(key.equalsIgnoreCase(""))
		        	key = name;
		        
		        result.put((String)pairs.getValue(), key);
		        
		    }
		} catch (NullPointerException e) {
			//e.printStackTrace();
			throw new ReadModelException(ErrorEnum.NO_BASE_URI.getMessage(), e);
		}
		
		return result;
	}
	
	private Model setPrefixes(Model model, Map<String, String> prefixes){
		
		Iterator<Entry<String,String>> it = prefixes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
	        String key = (String)pairs.getKey(); //URI
	        String value = (String)pairs.getValue(); //preffix name
	        model.setNsPrefix(value, key);
	    }
		
		return model;
	}
	
	private String getModelName(Model model){
		String prefix = model.getNsPrefixURI("");
		String[] tokens = prefix.split("/");
		String name = tokens[tokens.length-1];
		name = name.replace("#", "");
//		System.out.println(name);
		
		return name;
	}
	
	private Model readFile(String file, String lang){
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open( file );
		if (in == null) {
		    throw new IllegalArgumentException("ERROR: File: " + file + " not found");
		}
		// read the RDF/XML file
		model.read(in, "", lang);
		
		return model;
	}
	
	public Model getMyTripleStore() {
		return myTripleStore;
	}
	public void setMyTripleStore(Model myTripleStore) {
		this.myTripleStore = myTripleStore;
	}

	public Map<String,String> getMyPrefixes() {
		return myPrefixes;
	}

	public void setMyPrefixes(Map<String,String> myPrefixes) {
		this.myPrefixes = myPrefixes;
	}
	
}
