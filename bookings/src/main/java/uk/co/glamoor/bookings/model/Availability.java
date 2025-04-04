package uk.co.glamoor.bookings.model;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import uk.co.glamoor.bookings.exception.NoAvailableDateException;

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

	private Instant dateUtc;

	private String originalTimeZone;

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
		TimeSlot slot = new TimeSlot(time, time.plusMinutes(roundUpToNearestMultipleOf30(duration)));
		unavailableSlots.stream()
		        .filter(s -> s.getStartTime().equals(slot.getStartTime()) &&
                        s.getEndTime().equals(slot.getEndTime()) && s.getStatus() == Status.ESCROW)
		        .findFirst()
		        .ifPresent(s -> s.setStatus(Status.BOOKED));
	}
	
	public void unbookSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    TimeSlot slot = new TimeSlot(time, time.plusMinutes(roundUpToNearestMultipleOf30(duration)));
	    unavailableSlots.removeIf(unavailable -> unavailable.equals(slot) && unavailable.getStatus() == Status.BOOKED);
	}

	private boolean isSlotUnavailable(TimeSlot slot, TimeSlot unavailableSlot) {
		if (slot.getStatus() == Status.BOOKED || slot.getStatus() == Status.EXEMPTED) return true;

		if (slot.getStatus() == Status.ESCROW)
			return unavailableSlot.escrowUntil.isAfter(LocalDateTime.now());

		return slot.overlapsWith(unavailableSlot);
	}

	public void validateSlotAvailability(List<TimeSlot> unavailableSlots, TimeSlot requestedSlot) {
		boolean isUnavailable = unavailableSlots.stream()
				.anyMatch(unavailableSlot -> isSlotUnavailable(requestedSlot, unavailableSlot));

		if (isUnavailable) {
			throw new NoAvailableDateException("The requested slot no longer available.");
		}
	}


	public void escrowSlot(LocalTime time, int duration, int escrowDurationSeconds) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    TimeSlot slot = new TimeSlot(time, time.plusMinutes(roundUpToNearestMultipleOf30(duration)));

		validateSlotAvailability(unavailableSlots, slot);

		unavailableSlots.removeIf(slt -> (slt.getStatus() == Status.ESCROW && LocalDateTime.now().isAfter(slt.getEscrowUntil())));

		slot.setStatus(Status.ESCROW);
		slot.setEscrowUntil(LocalDateTime.now().plusSeconds(escrowDurationSeconds));

		unavailableSlots.add(slot);
	}

	public void pulseEscrowedSlot(LocalTime time, int duration, int escrowDurationSeconds) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Duration must be positive");
		}
		boolean updated = false;
		for (TimeSlot slot : unavailableSlots) {
			if (slot.getStartTime().equals(time) && slot.getEndTime().equals(time.plusMinutes(roundUpToNearestMultipleOf30(duration)))
					&& slot.getStatus() == Status.ESCROW) {
				slot.setEscrowUntil(LocalDateTime.now().plusSeconds(escrowDurationSeconds));
				updated = true;
				break;
			}
		}
		if (!updated) throw new IllegalStateException("Could not update timeslot");
	}
	
	public void unescrowSlot(LocalTime time, int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    unavailableSlots.removeIf(unavailableSlot -> unavailableSlot.getStartTime().equals(time)
				&& unavailableSlot.getEndTime().equals(time.plusMinutes(roundUpToNearestMultipleOf30(duration)))
				&& unavailableSlot.getStatus() == Status.ESCROW);
	}
	
	public List<TimeSlot> getAvailableSlots(int duration) {
		if (duration <= 0) {
	        throw new IllegalArgumentException("Duration must be positive");
	    }
	    List<TimeSlot> mergedUnavailableSlots = mergeUnavailableSlots(unavailableSlots);
	    List<TimeSlot> availableSlots = new ArrayList<>();

	    for (TimeSlot slot : timeSlots) {
	        List<TimeSlot> validSubSlots = splitSlot(slot, mergedUnavailableSlots);
	        
	        for (TimeSlot subSlot : validSubSlots) {
	            availableSlots.addAll(generateIntervals(subSlot, duration));
	        }
	    }

	    return availableSlots;
	}

	public int getMaxSlotDuration() {

		List<TimeSlot> mergedUnavailableSlots = mergeUnavailableSlots(unavailableSlots);
		List<TimeSlot> availableSlots = new ArrayList<>();

		int maxDuration = 0;

		for (TimeSlot slot : timeSlots) {
			List<TimeSlot> validSubSlots = splitSlot(slot, mergedUnavailableSlots);
			for (TimeSlot subSlot : validSubSlots) {
				maxDuration = Math.max(maxDuration, (int) subSlot.getDurationMinutes());
			}
		}

		return maxDuration;
	}

	private List<TimeSlot> mergeUnavailableSlots(List<TimeSlot> unavailableSlots) {

		unavailableSlots.removeIf(slot -> slot.getStatus() == Status.ESCROW
				&& slot.getEscrowUntil().isBefore(LocalDateTime.now()));

	    if (unavailableSlots.isEmpty()) {
	        return unavailableSlots;
	    }

	    List<TimeSlot> sortedSlots = new ArrayList<>(unavailableSlots);
	    sortedSlots.sort(Comparator.comparing(TimeSlot::getStartTime));
	    List<TimeSlot> merged = new ArrayList<>();

	    TimeSlot current = sortedSlots.get(0);

	    for (int i = 1; i < sortedSlots.size(); i++) {
	        TimeSlot next = sortedSlots.get(i);

	        if (current.getEndTime().isAfter(next.getStartTime()) || current.getEndTime().equals(next.getStartTime())) {
	            current = new TimeSlot(current.getStartTime(),
	                                   current.getEndTime().isAfter(next.getEndTime()) ? current.getEndTime() : next.getEndTime());
	        } else {
	            merged.add(current);
	            current = next;
	        }
	    }

	    merged.add(current);
	    return merged;
	}

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

	private List<TimeSlot> generateIntervals(TimeSlot slot, int duration) {
	    List<TimeSlot> intervals = new ArrayList<>();
	    LocalTime start = slot.getStartTime();
	    LocalTime end = slot.getEndTime();

	    while (start.plusMinutes(duration).isBefore(end) || start.plusMinutes(duration).equals(end)) {
	        intervals.add(new TimeSlot(start, start.plusMinutes(duration)));
	        start = start.plusMinutes(30); // Move to the next interval
	    }

	    return intervals;
	}

    @Data
	public static class TimeSlot {

		public TimeSlot(){}

		public TimeSlot(LocalTime startTime, LocalTime endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
		
	    private LocalTime startTime;
	    private LocalTime endTime;
	    private Status status;
		private LocalDateTime escrowUntil;
		private LocalDateTime escrowHeartbeatPulse;

	    public List<TimeSlot> subtract(TimeSlot unavailable) {
	        List<TimeSlot> result = new ArrayList<>();

	        if (!this.overlapsWith(unavailable)) {
	            result.add(this);
	        } else {
	            if (this.startTime.isBefore(unavailable.getStartTime())) {
	                result.add(new TimeSlot(this.startTime, unavailable.getStartTime()));
	            }
	            if (this.endTime.isAfter(unavailable.getEndTime())) {
	                result.add(new TimeSlot(unavailable.getEndTime(), this.endTime));
	            }
	        }

	        return result;
	    }

		public boolean overlapsWith(TimeSlot other) {
			return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
		}

	    public long getDurationMinutes() {
	        return Duration.between(startTime, endTime).toMinutes();
	    }


        @Override
		public int hashCode() {
			return Objects.hash(endTime, startTime, status);
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
			return Objects.equals(endTime, other.endTime) && Objects.equals(startTime, other.startTime) && status == other.status;
		}

		@Override
		public String toString() {
			return "TimeSlot [start=" + startTime + ", end=" + endTime + ", status=" + status + "]";
		}
		
		
	}
	
	public enum Status {
		AVAILABLE, BOOKED, EXEMPTED, ESCROW
	}

	public int roundUpToNearestMultipleOf30(int number) {
		int remainder = number % 30;
		if (remainder == 0) {
			return number;
		} else {
			return number + (30 - remainder);
		}
	}
}
