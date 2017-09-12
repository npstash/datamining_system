//
//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SEDMain.java (sed)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs;

import javax.swing.UIManager;

import com.nestor.cs.ui.SystemFrame;

public class Main
{

	public static class PleaseWait extends Thread
	{

		public boolean runme = true;

		public PleaseWait()
		{
			super();
			start();
		}

		@Override
        public void run()
		{
			while (this.runme)
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					return;
				}
				System.out.print("."); //$NON-NLS-1$
			}
			System.out.println();
		}

	}

	public static PleaseWait pleaseWait = null;

	public static final String REQ_JVM_VER = "1.5.0"; //$NON-NLS-1$

	public static void main(String[] args) throws Exception
	{
		String v = System.getProperty("java.version"); //$NON-NLS-1$
		System.out.println(Messages.getString("SEDMain.2") + v); //$NON-NLS-1$
		if (v.compareTo(REQ_JVM_VER) < 0)
		{
			System.out.println(Messages.getString("SEDMain.3") + REQ_JVM_VER); //$NON-NLS-1$
			System.exit(1);
		}
		pleaseWait = new PleaseWait();
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		SystemFrame.createFrame();
		pleaseWait.runme = false;
	}

}