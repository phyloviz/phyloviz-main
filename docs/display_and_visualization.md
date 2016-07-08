# Display and visualization

## Interface features

After running the selected algorithm, you will notice that the program then tries to optimize the display of the group with the largest number of elements in the data set. You can change the speed at which this occurs by moving the animation speed slider.

The Display tab offers the user the ability to search for an isolate, Highlight the SLVs and DLVs, control the animation speed, select diferent diferent or multiple groups. You can fit any displayed graphs to the window by right-clicking any open space (i.e. with no link or ST node) on the window.

Click to see how to use the features:

 1. *Common features*
   * [ST search](http://www.phyloviz.net/wiki/displayinterface/search.png),
   * [information area](http://www.phyloviz.net/wiki/displayinterface/InfoArea.png),
   * *Exporting Image*
   
 2. *GoEburst features*
   * [basic interface](http://www.phyloviz.net/wiki/displayinterface/BasicInterface.png),
   * [SLV/DLV highlighting](http://www.phyloviz.net/wiki/displayinterface/highlighting.png),
   * [high level edges](http://www.phyloviz.net/wiki/displayinterface/HighLevelEdges.png),
   * [changing group founder](http://www.phyloviz.net/wiki/displayinterface/ChangingGroupFounder.png),
   * [saving results](http://www.phyloviz.net/wiki/displayinterface/SavingResults.png).
   
 3. *GoEburst and Neighbor Joinning features*
   * [force control menu](http://www.phyloviz.net/wiki/displayinterface/ControlMenu.png),
 
 4. *Neighbor Joinning features** 
 
 5. *Hierarchical Clustering and Neighbor Joinning features*

## Color conventions

_Link colors for goeBURST results_:
* Black - Link drawn without recourse to tiebreak rules,
* Blue - Link drawn using tiebreak rule 1 (number of SLVs),
* Green - Link drawn using tiebreak rule 2 (number of DLVs),
* Red - Link drawn using tiebreak rule 3 (number of TLVs),
* Yellow - Link drawn using tiebreak rule 4 or 5 (Frequency found on the data set and ST number , respectively),
* Gray - Links drawn at DLV (darker gray) or TLV (lighter gray) if the groups are constructed at DLV/TLV level.

_Link colors for goeBURST Full MST results_: The goeBURST Full MST algorithm links uses a grayscale with darker links having less differences between the profiles than the lighter gray links. To know the number of differences that the link represents click on the link in the Display window.

_ST nodes colors_:
* Light green - Group founder
* Dark green - Sub-group founder
* Light blue - Common node
* Red - Selected node

