
package test.de.applejuicenet.client.gui.powerdownload;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.powerdownload.StandardAutomaticPwdlPolicy;

public class TestStandardAutomaticPwdlPolicy extends TestCase
{
	ApplejuiceFassadeDummy fassade = null;
	StandardAutomaticPwdlPolicy policy = null; 
		
	protected void setUp() throws Exception
	{
		super.setUp();
		fassade = new ApplejuiceFassadeDummy(new HashMap());
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3);
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
		fassade = null;
		policy = null;
	}

	public void testDoSimpleAction() throws IllegalArgumentException
	{
		try
		{
			policy.doAction();
		}
		catch (Exception e)
		{
			fail("Fehler: " + e.getMessage());
			e.printStackTrace();
		}
		fassade.verify();
	}

	public void testDoOneAction() throws IllegalArgumentException
	{
		Map<String,Download> downloads = new HashMap();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 1.1, Download.SUCHEN_LADEN, 1));
		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3);

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 1.1, Download.SUCHEN_LADEN, 3));
		try
		{
			policy.doAction();
		}
		catch (Exception e)
		{
			fail("Fehler: " + e.getMessage());
			e.printStackTrace();
		}
		fassade.verify();
	}

	public void testDoFourAction() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy(new HashMap());
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3);

		fassade.addDownload(new DownloadDummy(1, 0.0, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(2, 0.0, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(3, 1.3, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(4, 1.4, Download.SUCHEN_LADEN, 1));
		fassade.addExpectedDownload(new DownloadDummy(1, 0.0, Download.PAUSIERT, 1));
		fassade.addExpectedDownload(new DownloadDummy(2, 0.0, Download.PAUSIERT, 1)); 
		fassade.addExpectedDownload(new DownloadDummy(3, 1.3, Download.PAUSIERT, 1)); 
		fassade.addExpectedDownload(new DownloadDummy(4, 1.4, Download.SUCHEN_LADEN, 3)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testDoActionGroesse() throws Exception
	{
		Map<String,Download> downloads = new HashMap();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 10.0, Download.SUCHEN_LADEN, 1, 10));
		downloads.put(Integer.toString(2), new DownloadDummy(new Integer(2), "bla2", 10.0, Download.SUCHEN_LADEN, 1, 20));
		downloads.put(Integer.toString(3), new DownloadDummy(new Integer(3), "bla3", 1.3, Download.SUCHEN_LADEN, 1, 30));
		downloads.put(Integer.toString(4), new DownloadDummy(new Integer(4), "bla4", 1.4, Download.SUCHEN_LADEN, 1, 40));
		downloads.put(Integer.toString(5), new DownloadDummy(new Integer(5), "bla5", 10.0, Download.SUCHEN_LADEN, 1, 15));

		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3);

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 10.0, Download.PAUSIERT, 1, 10));
		fassade.addExpectedDownload(new DownloadDummy(new Integer(2), "bla2", 10.0, Download.SUCHEN_LADEN, 3, 20)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(3), "bla3", 1.3, Download.PAUSIERT, 1, 30)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(4), "bla4", 1.4, Download.PAUSIERT, 1, 40)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(5), "bla5", 10.0, Download.PAUSIERT, 1, 15)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testDoActionGroesseUndId() throws Exception
	{
		Map<String,Download> downloads = new HashMap();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 10.0, Download.SUCHEN_LADEN, 1, 100));
		downloads.put(Integer.toString(2), new DownloadDummy(new Integer(2), "bla2", 10.0, Download.SUCHEN_LADEN, 1, 20));
		downloads.put(Integer.toString(3), new DownloadDummy(new Integer(3), "bla3", 1.3, Download.SUCHEN_LADEN, 1, 30));
		downloads.put(Integer.toString(4), new DownloadDummy(new Integer(4), "bla4", 1.4, Download.SUCHEN_LADEN, 1, 40));

		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3);

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 10.0, Download.SUCHEN_LADEN, 3, 100));
		fassade.addExpectedDownload(new DownloadDummy(new Integer(2), "bla2", 10.0, Download.PAUSIERT, 1, 20)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(3), "bla3", 1.3, Download.PAUSIERT, 1, 30)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(4), "bla4", 1.4, Download.PAUSIERT, 1, 40)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testDoNextAction() throws Exception
	{
		Map<String,Download> downloads = new HashMap();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 1.1, Download.SUCHEN_LADEN, 1));
		downloads.put(Integer.toString(2), new DownloadDummy(new Integer(2), "bla2", 1.2, Download.SUCHEN_LADEN, 1));
		downloads.put(Integer.toString(3), new DownloadDummy(new Integer(3), "bla3", 1.3, Download.PAUSIERT, 3));
		downloads.put(Integer.toString(4), new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1));

		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 2, 3);

		policy.doAction();

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 1.1, Download.PAUSIERT, 1));
		fassade.addExpectedDownload(new DownloadDummy(new Integer(2), "bla2", 1.2, Download.SUCHEN_LADEN, 3)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(3), "bla3", 1.3, Download.SUCHEN_LADEN, 3)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1)); 
		
		fassade.verify();
	}

	public void testDoEndeAction() throws Exception
	{
		Map<String,Download> downloads = new HashMap();
		downloads.put(Integer.toString(1), new DownloadDummy(new Integer(1), "bla1", 1.1, Download.FERTIG, 1));
		downloads.put(Integer.toString(2), new DownloadDummy(new Integer(2), "bla2", 1.2, Download.FERTIG, 1));
		downloads.put(Integer.toString(3), new DownloadDummy(new Integer(3), "bla3", 1.3, Download.FERTIG, 3));
		downloads.put(Integer.toString(4), new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1));

		fassade = new ApplejuiceFassadeDummy(downloads);
		policy = new StandardAutomaticPwdlPolicy(fassade, 2, 3);

		policy.doAction();

		fassade.addExpectedDownload(new DownloadDummy(new Integer(1), "bla1", 1.1, Download.FERTIG, 1));
		fassade.addExpectedDownload(new DownloadDummy(new Integer(2), "bla2", 1.2, Download.FERTIG, 1)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(3), "bla3", 1.3, Download.FERTIG, 3)); 
		fassade.addExpectedDownload(new DownloadDummy(new Integer(4), "bla4", 1.4, Download.FERTIG, 1)); 
		
		fassade.verify();
	}

	public void testDoActionOneAdded() throws Exception
	{
		fassade = new ApplejuiceFassadeDummy(new HashMap());
		policy = new StandardAutomaticPwdlPolicy(fassade, 1, 3);

		fassade.addDownload(new DownloadDummy(1, 10.0, Download.SUCHEN_LADEN, 1));
		fassade.addDownload(new DownloadDummy(2, 10.0, Download.SUCHEN_LADEN, 1));
		fassade.addExpectedDownload(new DownloadDummy(1, 10.0, Download.SUCHEN_LADEN, 3));
		fassade.addExpectedDownload(new DownloadDummy(2, 10.0, Download.PAUSIERT, 1)); 

		policy.doAction();
		fassade.verify();

		fassade.addDownload(new DownloadDummy(3, 0.0, Download.SUCHEN_LADEN, 1));
		fassade.addExpectedDownload(new DownloadDummy(3, 0.0, Download.PAUSIERT, 1)); 
		
		policy.doAction();
		fassade.verify();
	}

	public void testGetVersion()
	{
		assertNotNull("Version ist nicht gesetzt!", policy.getVersion());
		assertTrue("Version ist \"\"!", policy.getVersion().length()>0);
	}

	public void testGetDescription()
	{
		assertNotNull("Description ist nicht gesetzt!", policy.getDescription());
		assertTrue("Description ist \"\"!", policy.getDescription().length()>0);
	}

	public void testGetAuthor()
	{
		assertNotNull("Autor ist nicht gesetzt!", policy.getAuthor());
		assertTrue("Autor ist \"\"!", policy.getAuthor().length()>0);
	}

}
