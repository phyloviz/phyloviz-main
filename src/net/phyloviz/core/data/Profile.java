/*
 * @(#)Profile.java 27/01/11
 *
 * Copyright 2011 Phyloviz. All rights reserved.
 * Use is subject to license terms.
 */

package net.phyloviz.core.data;
/**
 * This interface describes the minimal requirements for an isolate
 * profile created by a microbial typing method.  An isolate represents a sampled
 * microorganism of a bacterial population. The application of a typing
 * method to an isolate gives a characterization at the strain level of the isolate,
 * i.e., an isolate profile.
 * Thus, each profile consists of the list of features that characterises an
 * isolate with respect to a specific typing method.
 * @since   PHILOViZ 1.0
 * @author A. P. Francisco
 *
 */
public interface Profile{

   /**
     * Returns a <tt>int</tt>  that represents the internal identifier of this typing
     * profile.
     *
     * @return the internal value of the identifier of this typing profile.
     *
     */

    public int getUID();

    /**
     * Returns a <tt>String</tt>  that represents the identifier of this typing
     * profile.
     *
     * @return the identifier of this typing profile.
     */
    public String getID();

     /**
     * Returns the number of features of this profile.
     * If contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this collection.
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

     /**
     * Returns the frequency of this typing
     * profile among all the isolates.
     *
     * @return the frequency of this typing profile.
     *
     */
    public int getFreq();

    /**
     * This method updates the frequency of this typing
     * profile.
     *
     * @param freq the new frequency of this typing profile.
     * @void updates the frequency of this typing profile.
     *
     */
    public void setFreq(int freq);

}
