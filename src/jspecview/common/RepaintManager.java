package jspecview.common;

public class RepaintManager {

	public RepaintManager(JSViewer viewer) {
		this.vwr = viewer;
	}
  /////////// thread management ///////////
  
  boolean repaintPending;
	private JSViewer vwr;

//  private int n;
  public boolean refresh() {
  	//n++;
    if (repaintPending) {
    	//System.out.println("Repaint " + n + " skipped");
      return false;
    }
    repaintPending = true;
    vwr.pd().taintedAll = true;
    @SuppressWarnings("unused")
		Object applet = vwr.html5Applet;
    /**
     * @j2sNative
     * 
     *  if (typeof Jmol != "undefined" && Jmol._repaint && applet) 
     *    Jmol._repaint(applet, false);
     *  this.repaintDone();
     */
    {
    	vwr.selectedPanel.repaint();
    }
    return true;
  }

  synchronized public void repaintDone() {
    repaintPending = false;
      notify(); // to cancel any wait in requestRepaintAndWait()
  }
}
