package uk.co.glamoor.stylists.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="booking-availabilities")
@CompoundIndexes({
    @CompoundIndex(name = "stylist_service_provider_date_index", def = "{'stylistId': 1, 'serviceProviderId': 1, 'date': 1}")
})
public class Availability {
	
	@Id
	private String id;
	private String stylistId;
	private String serviceProviderId;
	private LocalDate date;
	private List<TimeSlot> timeSlots = new ArrayList<>();
	private List<TimeSlot> unavailableSlots = new ArrayList<>();
	

	public void excemptSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
		TimeSlot slot = new TimeSlot(time, time.plusMinutes(duration));
		slot.setStatus(Status.EXEMPTED);
		unavailableSlots.add(slot);
	}
	
	public void bookSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    TimeSlot slot = new TimeSlot(time, time.plusMinutes(duration));
	    if (unavailableSlots.stream().anyMatch(slot::overlapsWith)) {
	        throw new IllegalStateException("The requested slot overlaps with an unavailable slot.");
	    }
		slot.setStatus(Status.BOOKED);
		unavailableSlots.add(slot);
	}
	
	public void bookEscrowedSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
		TimeSlot slot = new TimeSlot(time, time.plusMinutes(duration));
		unavailableSlots.stream()
		        .filter(s -> s.equals(slot) && s.getStatus() == Status.ESCROWED)
		        .findFirst()
		        .ifPresent(s -> s.setStatus(Status.BOOKED));
	}
	
	public void unbookSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    TimeSlot slot = new TimeSlot(time, time.plusMinutes(duration));
	    unavailableSlots.removeIf(unavailable -> unavailable.equals(slot) && unavailable.getStatus() == Status.BOOKED);
	}
	
	public void escrowSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    TimeSlot slot = new TimeSlot(time, time.plusMinutes(duration));
	    if (unavailableSlots.stream().anyMatch(slot::overlapsWith)) {
	        throw new IllegalStateException("The requested slot overlaps with an unavailable slot.");
	    }
		slot.setStatus(Status.ESCROWED);
		unavailableSlots.add(slot);
	}
	
	public void unescrowSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    TimeSlot slot = new TimeSlot(time, time.plusMinutes(duration));
	    unavailableSlots.removeIf(unavailable -> unavailable.equals(slot) && unavailable.getStatus() == Status.ESCROWED);
	}
	
	public List<TimeSlot> getAvailableSlots(int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    // Merge overlapping unavailable slots to simplify logic
	    List<TimeSlot> mergedUnavailableSlots = mergeUnavailableSlots(unavailableSlots);
	    List<TimeSlot> availableSlots = new ArrayList<>();

	    // Iterate over all available time slots
	    for (TimeSlot slot : timeSlots) {
	        // Split the current slot by merged unavailable slots
	        List<TimeSlot> validSubSlots = splitSlot(slot, mergedUnavailableSlots);
	        
	        // Generate intervals of the given duration from the valid sub-slots
	        for (TimeSlot subSlot : validSubSlots) {
	            availableSlots.addAll(generateIntervals(subSlot, duration));
	        }
	    }

	    return availableSlots;
	}

	// Helper method to merge overlapping unavailable slots
	private List<TimeSlot> mergeUnavailableSlots(List<TimeSlot> unavailableSlots) {
	    if (unavailableSlots.isEmpty()) {
	        return unavailableSlots;
	    }

	    List<TimeSlot> sortedSlots = new ArrayList<>(unavailableSlots);
	    sortedSlots.sort(Comparator.comparing(TimeSlot::getStart));
	    List<TimeSlot> merged = new ArrayList<>();
	    TimeSlot current = sortedSlots.get(0);

	    for (int i = 1; i < sortedSlots.size(); i++) {
	        TimeSlot next = sortedSlots.get(i);

	        // If current and next overlap, merge them
	        if (current.getEnd().isAfter(next.getStart()) || current.getEnd().equals(next.getStart())) {
	            current = new TimeSlot(current.getStart(), 
	                                   current.getEnd().isAfter(next.getEnd()) ? current.getEnd() : next.getEnd());
	        } else {
	            // Add the current slot to the merged list and update current
	            merged.add(current);
	            current = next;
	        }
	    }

	    // Add the last processed slot
	    merged.add(current);
	    return merged;
	}

	// Helper method to split a slot by unavailable slots
	private List<TimeSlot> splitSlot(TimeSlot slot, List<TimeSlot> unavailableSlots) {
	    List<TimeSlot> result = new ArrayList<>();
	    result.add(slot);

	    for (TimeSlot unavailable : unavailableSlots) {
	        List<TimeSlot> temp = new ArrayList<>();
	        for (TimeSlot current : result) {
	            temp.addAll(current.subtract(unavailable));
	        }
	        result = temp;
	    }

	    return result;
	}

	// Helper method to generate intervals of the given duration
	private List<TimeSlot> generateIntervals(TimeSlot slot, int duration) {
	    List<TimeSlot> intervals = new ArrayList<>();
	    LocalTime start = slot.getStart();
	    LocalTime end = slot.getEnd();

	    // Generate intervals with a 30-minute step
	    while (start.plusMinutes(duration).isBefore(end) || start.plusMinutes(duration).equals(end)) {
	        intervals.add(new TimeSlot(start, start.plusMinutes(duration)));
	        start = start.plusMinutes(30); // Move to the next interval
	    }

	    return intervals;
	}

	public static class TimeSlot {
		
		public TimeSlot() {}
		
		public TimeSlot(LocalTime start, LocalTime end) {
			this.start = start;
			this.end = end;
		}
		
	    private LocalTime start;
	    private LocalTime end;
	    private Status status;
	    
	    public List<TimeSlot> subtract(TimeSlot unavailable) {
	        List<TimeSlot> result = new ArrayList<>();

	        if (!this.overlapsWith(unavailable)) {
	            result.add(this); // No overlap, keep the entire slot
	        } else {
	            if (this.start.isBefore(unavailable.getStart())) {
	                result.add(new TimeSlot(this.start, unavailable.getStart())); // Before unavailable
	            }
	            if (this.end.isAfter(unavailable.getEnd())) {
	                result.add(new TimeSlot(unavailable.getEnd(), this.end)); // After unavailable
	            }
	        }

	        return result;
	    }
	    
	    public boolean overlapsWith(TimeSlot other) {
	        return (start.isBefore(other.end) && end.isAfter(other.start));
	    }

	    public long getDurationMinutes() {
	        return Duration.between(start, end).toMinutes();
	    }
	    
	    
		public LocalTime getStart() {
			return start;
		}
		public void setStart(LocalTime start) {
			this.start = start;
		}
		public LocalTime getEnd() {
			return end;
		}
		public void setEnd(LocalTime end) {
			this.end = end;
		}
		public Status getStatus() {
			return status;
		}
		public void setStatus(Status status) {
			this.status = status;
		}
		

		@Override
		public int hashCode() {
			return Objects.hash(end, start, status);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TimeSlot other = (TimeSlot) obj;
			return Objects.equals(end, other.end) && Objects.equals(start, other.start) && status == other.status;
		}

		@Override
		public String toString() {
			return "TimeSlot [start=" + start + ", end=" + end + ", status=" + status + "]";
		}
		
		
	}
	
	public static enum Status {
		AVAILABLE, BOOKED, EXEMPTED, ESCROWED
	}
}

