package de.applejuicenet.client.gui.tables;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/Attic/MergeSort.java,v 1.4 2004/10/11 18:18:51 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 */

public abstract class MergeSort
    extends Object {
    protected Object toSort[];
    protected Object swapSpace[];

    public void sort(Object array[]) {
        if (array != null && array.length > 1) {
            int maxLength;

            maxLength = array.length;
            swapSpace = new Object[maxLength];
            toSort = array;
            this.mergeSort(0, maxLength - 1);
            swapSpace = null;
            toSort = null;
        }
    }

    public abstract int compareElementsAt(int beginLoc, int endLoc);

    protected void mergeSort(int begin, int end) {
        if (begin != end) {
            int mid;

            mid = (begin + end) / 2;
            this.mergeSort(begin, mid);
            this.mergeSort(mid + 1, end);
            this.merge(begin, mid, end);
        }
    }

    protected void merge(int begin, int middle, int end) {
        int firstHalf, secondHalf, count;

        firstHalf = count = begin;
        secondHalf = middle + 1;
        while ( (firstHalf <= middle) && (secondHalf <= end)) {
            if (this.compareElementsAt(secondHalf, firstHalf) < 0) {
                swapSpace[count++] = toSort[secondHalf++];
            }
            else {
                swapSpace[count++] = toSort[firstHalf++];
            }
        }
        if (firstHalf <= middle) {
            while (firstHalf <= middle) {
                swapSpace[count++] = toSort[firstHalf++];
            }
        }
        else {
            while (secondHalf <= end) {
                swapSpace[count++] = toSort[secondHalf++];
            }
        }
        for (count = begin; count <= end; count++) {
            toSort[count] = swapSpace[count];
        }
    }
}
