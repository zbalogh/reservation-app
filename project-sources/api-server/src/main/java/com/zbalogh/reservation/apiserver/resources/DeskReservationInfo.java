package com.zbalogh.reservation.apiserver.resources;

import java.util.ArrayList;
import java.util.List;

import com.zbalogh.reservation.apiserver.utils.AppConstants;

public class DeskReservationInfo {

	private final List<Long> reservationList = new ArrayList<Long>();
	
	public DeskReservationInfo() {
		super();
	}
	
	public void addValue(Long value)
	{
		reservationList.add(value);
	}
	
	public void addValue(int index, Long value)
	{
		reservationList.set(index, value);
	}
	
	public void initList(int count)
	{
		clearList();
		
		for (int i=0; i<count; i++) {
			reservationList.add(0L);
		}
	}
	
	public void clearList()
	{
		reservationList.clear();
	}

	public List<Long> getReservationList()
	{
		return reservationList;
	}

	public String getAppInstanceId()
	{
		return AppConstants.APP_INSTANCE_UUID;
	}
}
