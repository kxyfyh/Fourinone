package com.fourinone.tests.IntegratedDemo;
import java.io.Serializable;

import com.fourinone.Contractor;
import com.fourinone.WareHouse;
import com.fourinone.WorkerLocal;
import com.sun.corba.se.pept.transport.Selector;

public class CtorDemo extends Contractor {
	private String	ctorname;

	CtorDemo(String ctorname) {
		this.ctorname = ctorname;
	}

	public static class MyClass implements Runnable, Serializable {
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1011239325080111941L;
		String	str;

		public MyClass(String str) {
			super();
			this.str = str;
		}

		public void run() {
			System.out.println("run:" + str);
		}
	}

	public WareHouse giveTask(WareHouse inhouse) {
		WorkerLocal[] wks = getWaitingWorkers("workdemo");
		System.out.println("wks.length:" + wks.length);

		String outStr = inhouse.getString("id");
		WareHouse[] hmarr = new WareHouse[wks.length];

		int data = 0;
		for (int j = 0; j < 1;) {
			for (int i = 0; i < wks.length; i++) {
				if (hmarr[i] == null) {
					WareHouse wh = new WareHouse();
					wh.put("run", new MyClass(ctorname + (data)));
					wh.put("id", ctorname + (data++));

					hmarr[i] = wks[i].doTask(wh);
				} else if (hmarr[i].getStatus() != WareHouse.NOTREADY) {
					System.out.println(hmarr[i]);
					outStr += hmarr[i];
					hmarr[i] = null;
					j++;
				}
			}
		}

		inhouse.setString("id", outStr);
		return inhouse;
	}

	public static void main(String[] args) {
		Contractor a = new CtorDemo("OneCtor");
		a.toNext(new CtorDemo("TwoCtor")).toNext(new CtorDemo("ThreeCtor"));
		WareHouse house = new WareHouse("id", "begin ");
		System.out.println(a.giveTask(house, true));
		System.out.println("end");
	}
}