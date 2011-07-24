package jp.lahan.autofilecleaner;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileCleaner {
	
	public static int clean(String dir, int fileNum) throws FileCleanException{
		File[] list = listFiles(dir);
		int i=0;
		for(i=0; i<list.length - fileNum; ++i){
			if(!list[i].delete()){
				throw new FileCleanException(list[i].getPath() + " can not be deleted");
			}
		}
		return i;
	}

	private static File[] listFiles(String dir) throws FileCleanException{
		File file = new File(dir);
		if(!file.exists()){
			throw new FileCleanException(dir + " does not exist");
		}
		
		File[] list = file.listFiles();
		Arrays.sort(list, new Comparator<File>() {
			@Override
			public int compare(File object1, File object2) {
				return new Long(object1.lastModified()).compareTo(new Long(object2.lastModified()));
			}						
		});
				
		return list;
	}
	
	
	public static class FileCleanException extends Exception{
		private static final long serialVersionUID = 1L;
		
		public FileCleanException(String msg) {
			super(msg);
		}
	}
}
