package net.phyloviz.category.color;

import java.awt.Color;

public class Palette {

	private Color[]  colors;

	public Palette(int n) {
		generateColors(n);
	}

	public Color[] getColors(){
		return colors;
	}

        public void setColor(int i,Color newColor){
            if(i>=0 && i<colors.length)
                colors[i]=newColor;
        }

	private void generateColors(int n){
		colors=new Color[n];
		float prevColor=0,prevBri=0;
       if (n == 0) n = 1; // para sair... desnecessário!?
		int x= (int)(Math.ceil(colors.length/3f));
		int step=1;
		if (x != 0) // para sair... desnecess�rio!?
			step=(120/x);
		float cor, sat, bri;
		float satDiff[]={80,100,90,70,70,90,100,80};
		float briDiff[]={100,70,85,65};
		for(int i=0,j=0;i<colors.length;i=i+3,j++){
			cor=(n + j*step)%120;
			sat=satDiff[j%satDiff.length];
			bri=briDiff[j%briDiff.length];
			if(i!=0 && (prevColor-cor<=15 && prevBri-bri<=30)){
				bri=bri-20;
			}
			prevColor=cor;
			prevBri=cor;
			colors[i]=Color.getHSBColor((cor/360f), (sat/100f), bri/100f);
		}
		for(int i=2,j=0;i<colors.length;i=i+3,j++){
			cor=(n/2+j*step+120)%240;
			sat=satDiff[j%satDiff.length];
			bri=briDiff[j%briDiff.length];
			if(i>2 && (prevColor-cor<=15 && prevBri-bri<=30)){
				bri=bri-20;
			}
			colors[i]=Color.getHSBColor((cor/360f),sat/100f, bri/100f);
		}
		for(int i=1,j=0;i<colors.length;i=i+3,j++){
			cor=(j*step+240+ n/3)%360;
			sat=satDiff[j%satDiff.length];
			bri=briDiff[j%briDiff.length];
			if(i>1 && (prevColor-cor<=15 && prevBri-bri<=30)){
				bri=bri-20;
			}
			colors[i]=Color.getHSBColor((cor/360f), sat/100f, bri/100f);

		}

	}
}
