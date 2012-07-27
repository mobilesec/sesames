package at.sesame.fhooe;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XmlFileFilter 
extends FileFilter 
{
	private static final String XML_EXTENSION = "xml";
	
	/**
	 * checks if the passed file's extension matches XML_EXTENSION
	 * @param f the file to check 
	 * @return true if the file is accepted, false otherwise
	 */
	public boolean accept(File f) 
	{
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) 
		{
			if (extension.equals(XML_EXTENSION)) 
			{
				return true;
			} 
			else 
			{
				return false;
			}
		}
		return false;
	}

    /**
     * returns the extension of a passed file
     * @param f the file to get the extension from
     * @return the extension of the file
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    /**
     * returns the description of the filtered files
     * @return the description of the filtered files
     */
    public String getDescription() {
        return "XML Files";
    }
}
