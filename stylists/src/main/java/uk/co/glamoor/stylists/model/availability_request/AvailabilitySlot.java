package uk.co.glamoor.stylists.model.availability_request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import lombok.Data;

@Data
public class AvailabilitySlot {
	private String stylistId;
	private String serviceProviderId;
	private LocalDate date;
	private LocalTime time;
	private int duration;
	
	public AvailabilitySlot(String stylistId, String serviceProviderId, LocalDate date, LocalTime time,
			int duration) {
		super();
		this.stylistId = stylistId;
		this.serviceProviderId = serviceProviderId;
		this.date = date;
		this.time = time;
		this.duration = duration;
	}
	public String getStylistId() {
		return stylistId;
	}
	public void setStylistId(String stylistId) {
		this.stylistId = stylistId;
	}
	public String getServiceProviderId() {
		return serviceProviderId;
	}
	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "AvailabilitySlotRequest [stylistId=" + stylistId + ", serviceProviderId=" + serviceProviderId
				+ ", date=" + date + ", time=" + time + ", duration=" + duration + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(date, duration, serviceProviderId, stylistId, time);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AvailabilitySlot other = (AvailabilitySlot) obj;
		return Objects.equals(date, other.date) && duration == other.duration
				&& Objects.equals(serviceProviderId, other.serviceProviderId)
				&& Objects.equals(stylistId, other.stylistId) && Objects.equals(time, other.time);
	}
	
}
