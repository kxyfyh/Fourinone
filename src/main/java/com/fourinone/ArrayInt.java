package com.fourinone;

final class ArrayInt extends ArrayAdapter implements ArrayAdapter.ListInt{
	private int arrsize = 0x20000;
	private int[] arrint;
	private int arrindex = 0;
	
	public ArrayInt(){
		super();
		arrint = new int[arrsize];
		objarr[objindex++]=arrint;
	}
	
	public void add(int[] initarr){
		for(int i:initarr)
			add(i);
	}
	
	public void add(int i){
		if(arrindex==arrint.length){
			arrint = new int[arrsize];
			auto();
			objarr[objindex++]=arrint;
			arrindex=0;
		}
		arrint[arrindex++]=i;
	}
	
	public int size(){
		//System.out.println("objindex:"+objindex+",arrindex:"+arrindex);
		return (objindex-1)*arrsize+arrindex;
	}
	
	public void set(int index, int i){
		((int[])objarr[index/arrsize])[index%arrsize]=i;
	}
	
	public int get(int index){
		return ((int[])objarr[index/arrsize])[index%arrsize];
	}
	
	public void sort(){
		IntSort is = new IntSort();
		is.arrsort(0, size()-1);
	}
	
	public int[] sort(int[] arr){
		IntSort is = new IntSort(arr);
		is.intsort(0, arr.length-1);
		return arr;
	}
	
	private class IntSort{
		private int[] arr;
		IntSort(){}
		IntSort(int[] arr){
			this.arr = arr;
		}
		
		private void intsort(int k, int m)
		{ 
			int j=m,i=k; 
			for(;i<j;i++){ 
				int vai = arr[i];
				while(vai<=arr[i+1]){ 
					if(j==i+1)break; 
					int vai1 = arr[i+1]; 
					arr[i+1]=arr[j];
					arr[j--]=vai1; 
				} 
				
				if(vai>arr[i+1]){ 
					arr[i]=arr[i+1]; 
					arr[i+1]=vai; 
				} 
			} 
			if(i-1>k) 
				intsort(k,i-1); 
			if(m>i) 
				intsort(i,m); 
		}
		
		private void arrsort(int k, int m)
		{ 
			int j=m,i=k; 
			for(;i<j;i++){ 
				int vai = get(i);
				while(vai<=get(i+1)){ 
					if(j==i+1)break; 
					int vai1 = get(i+1);
					set(i+1, get(j));
					set(j--, vai1);
				} 
				
				if(vai>get(i+1)){ 
					set(i, get(i+1));
					set(i+1, vai);
				} 
			} 
			if(i-1>k) 
				arrsort(k,i-1); 
			if(m>i) 
				arrsort(i,m); 
		}
	}
}