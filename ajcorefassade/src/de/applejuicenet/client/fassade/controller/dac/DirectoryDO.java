package de.applejuicenet.client.fassade.controller.dac;

import java.util.ArrayList;
import java.util.List;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/DirectoryDO.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r <aj@tkl-soft.de>
 * 
 */

public class DirectoryDO {
	public static final int TYPE_RECHNER = 1;
	public static final int TYPE_LAUFWERK = 2;
	public static final int TYPE_DISKETTE = 3;
	public static final int TYPE_ORDNER = 4;
	public static final int TYPE_DESKTOP = 5;

	private static String separator;

	private String name;
	private int type;
	private boolean fileSystem;
	private String path;
	private List<DirectoryDO> children = null;

	public DirectoryDO(String name, int type, boolean fileSystem, String path) {
		this.name = name;
		this.type = type;
		this.fileSystem = fileSystem;
		this.path = path;
	}

	public static String getSeparator() {
		return separator;
	}

	public static void setSeparator(String separator) {
		DirectoryDO.separator = separator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(boolean fileSystem) {
		this.fileSystem = fileSystem;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void addChild(DirectoryDO directoryDO) {
		if (directoryDO == null) {
			return;
		}
		if (children == null) {
			children = new ArrayList<DirectoryDO>();
		}
		children.add(directoryDO);
	}

	public DirectoryDO[] getChildren() {
		if (children == null) {
			return null;
		}
		return (DirectoryDO[]) children
				.toArray(new DirectoryDO[children.size()]);
	}
}
