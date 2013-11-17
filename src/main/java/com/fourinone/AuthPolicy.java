package com.fourinone;

public enum AuthPolicy{
	OP_READ(1),OP_READ_WRITE(3),OP_ALL(7);
	private int policy;
	AuthPolicy(int policy){
		this.policy = policy;
	}
	public int getPolicy(){
		return policy;
	}
	public static boolean authIncluded(int targetAuth, int curAuth){
		return ((targetAuth&curAuth)==targetAuth)?true:false;
	}
	public static void main(String[] args)
	{
		System.out.println(authIncluded(AuthPolicy.OP_ALL.getPolicy(),AuthPolicy.OP_READ_WRITE.getPolicy()));//ParkAuth.OP_READ
	}
	
	/*
	public static final int OP_READ = 1;
	public static final int OP_READ_WRITE = 3;
	public static final int OP_ALL = 7;
	
	public static boolean authIncluded(int targetAuth, int curAuth){
		return ((targetAuth&curAuth)==targetAuth)?true:false;
	}
	
	public static int getAuthPolicy(ParkAuth auth){
		int policy = OP_READ;
		switch(auth){
            case READ:
                break;
            case READ_WRITE:    
                policy = OP_READ_WRITE;
                break;
            case ALL:    
                policy = OP_ALL;
                break;              
        }
        return policy;
	}
	
	public static void main(String[] args)
	{
		//if((AuthPolicy.OP_READ&AuthPolicy.OP_READ_WRITE)==AuthPolicy.OP_READ)
		System.out.println(authIncluded(AuthPolicy.OP_READ_WRITE,AuthPolicy.OP_ALL));
	}
	*/
}