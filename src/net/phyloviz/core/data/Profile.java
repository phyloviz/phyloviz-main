package net.phyloviz.core.data;
/**
 * This interface describes the minimal requirements for an isolate
 * profile created by a typing method.
 * Each profile consists of the list of features that characterises a specific
 * isolate.
 * @since   PHILOViZ 1.0
 * @author A. P. Francisco
 * 
 */
public interface Profile{
    /**
     * Returns a <tt>String</tt>  that represents the identifier of this typing
     * profile.
     * 
     * @return the identifier of this typing profile
     */
    public String getID();

     /**
     * Returns the number of features of this profile.
     * If contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this collection
     */
    public int profileLength();
    
    /**
     * Returns  the value of a feature of this
     * profile as a string that is at the specified index. An index ranges from
     * <code>0</code> to <code>profileLenght() - 1</code>.
     * The first description corresponds to the feature that
     * is at index <code>0</code>, the next at index <code>1</code>,
     * and so on, as for array indexing.
     *
     *
     * @param      idx the index of the description of a feature.
     * @return     the <code>String</code>  that gives the description 
     *             of a feature at the specified index of the list of features .
     *             The first <code>String</code> is at index <code>0</code>.
     * @exception  IndexOutOfBoundsException  if the <code>idx</code>
     *             argument is negative or not less than the length of this
     *             <code>profileLenght()</code>.
     */
    public String getValue(int idx);
    
}
