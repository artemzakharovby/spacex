package com.six.spacex.domain;

import com.six.spacex.InternalSpaceXException;
import com.six.spacex.domain.id.MissionId;
import com.six.spacex.domain.id.RocketId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Mission implements SpaceXObject {

    private static final Map<MissionStatus, Set<MissionStatus>> TRANSITIONS = Map.of(
            MissionStatus.SCHEDULED, Set.of(MissionStatus.PENDING, MissionStatus.IN_PROGRESS),
            MissionStatus.PENDING, Set.of(MissionStatus.SCHEDULED),
            MissionStatus.IN_PROGRESS, Set.of(MissionStatus.ENDED)
    );

    static {
        MissionStatus[] rocketStatuses = MissionStatus.values();
        if (TRANSITIONS.size() < rocketStatuses.length) {
            throw new InternalSpaceXException(
                    "Not all mission statuses are handled. TRANSITIONS: {0}, RocketStatuses: {1}",
                    TRANSITIONS, Arrays.stream(rocketStatuses).toList()
            );
        }
    }

    private static final int INVALID_NUMBER_OF_ROCKETS = 0;

    private final MissionId id;
    private final String name;
    private final MissionStatus status;
    private final Map<RocketId, Rocket> rockets;

    public Mission(MissionId id, String name) {
        this(id, name, MissionStatus.SCHEDULED, Map.of());
    }

    public Mission(MissionId id, String name, List<Rocket> rockets) {
        this(id, name, MissionStatus.SCHEDULED,
                // Rockets are assigned to mission.
                rockets.stream().collect(Collectors.toMap(Rocket::getId, rocket -> rocket.markAsAssigned(id)))
        );
    }

    private Mission(MissionId id, String name, MissionStatus status, Map<RocketId, Rocket> rockets) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.rockets = rockets;
        validate();
    }

    public Mission markAsEnded() {
        isTransitionAllowed(status, MissionStatus.ENDED);
        return new Mission(id, name, MissionStatus.ENDED, Map.of());
    }

    public Mission markAsInProgress(RocketId... rocketIds) {
        isTransitionAllowed(status, MissionStatus.IN_PROGRESS);
        // "no repairing rockets" validation is not needed, because they will always have ON_GROUND status.
        // check markAsScheduled
        if (rockets.isEmpty()) {
            throw new InvalidObjectStateException("Mission cannot be started because there are no rockets. Mission: {0}", this);
        }
        if (rocketIds.length == INVALID_NUMBER_OF_ROCKETS) {
            throw new InvalidObjectStateException(
                    "Mission cannot be started because the number of rockets to be started is invalid. Mission: {0}", this
            );
        }

        Map<RocketId, Rocket> updatedRockets = new HashMap<>(rockets.size());
        updatedRockets.putAll(rockets);

        for (RocketId rocketId: rocketIds) {
            Rocket rocket = rockets.get(rocketId);
            if (rocket == null) {
                throw new InvalidObjectStateException(
                        "Mission cannot be started because rocket with ID {0} was not found. Mission: {1}", rocketId, this
                );
            }
            updatedRockets.put(rocketId, rocket.markAsInSpace());
        }
        return new Mission(id, name, MissionStatus.IN_PROGRESS, updatedRockets);
    }

    public Mission markAsScheduled() {
        isTransitionAllowed(status, MissionStatus.SCHEDULED);

        // rockets repaired
        Map<RocketId, Rocket> repairedRockets = changeRocketsStatus(
                rocket -> (rocket.getStatus() == RocketStatus.IN_REPAIR) ? rocket.markAsRepaired() : rocket
        );
        return new Mission(id, name, MissionStatus.SCHEDULED, repairedRockets);
    }

    public Mission markAsPending(RocketId... rocketIds) {
        validateBeforePending(rocketIds);

        // maybe refactor with map.replaceAll?
        for (RocketId rocketId: rocketIds) {
            Rocket rocket = rockets.get(rocketId);
            if (rocket == null) {
                throw new InvalidObjectStateException(
                        "Mission cannot be marked as pending because there is no rocket with ID {0}. Mission: {1}", rocketId, this
                );
            }
            Rocket inRepairing = rocket.markAsRepairing();
            rockets.put(inRepairing.getId(), inRepairing);
        }

        return new Mission(id, name, MissionStatus.PENDING, rockets);
    }

    public Mission assignRockets(Rocket... rocketsToAssign) {
        validateBeforeAssigning(rocketsToAssign);

        Map<RocketId, Rocket> newRockets = new HashMap<>(rockets);
        for (Rocket rocket: rocketsToAssign) {
            newRockets.put(rocket.getId(), rocket.markAsAssigned(id));
        }

        return new Mission(id, name, status, newRockets);
    }

    public int getRocketsNumber() {
        return rockets.size();
    }

    public MissionId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public Map<RocketId, Rocket> getRockets() {
        return rockets;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(id, mission.id) && Objects.equals(name, mission.name) && status == mission.status && Objects.equals(rockets, mission.rockets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, rockets);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mission{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", status=").append(status);
        sb.append(", rockets=").append(rockets);
        sb.append('}');
        return sb.toString();
    }

    private void validateBeforeAssigning(Rocket... rocketsToAssign) {
        if (status == MissionStatus.IN_PROGRESS || status == MissionStatus.ENDED) {
            throw new InvalidObjectStateException(
                    "Rockets cannot be assigned because mission has invalid status. Mission: {0}", this
            );
        }
        if (rocketsToAssign == null || rocketsToAssign.length == INVALID_NUMBER_OF_ROCKETS) {
            throw new InvalidObjectStateException("Rockets is null or the number is invalid. Mission: {0}", this);
        }
        for (Rocket rocket: rocketsToAssign) {
            if (this.rockets.containsKey(rocket.getId())) {
                throw new InvalidObjectStateException("Rocket is already assigned to this mission. Mission: {0}", this);
            }
        }
    }

    private void validateBeforePending(RocketId... rocketId) {
        isTransitionAllowed(status, MissionStatus.PENDING);
        if (rockets.isEmpty()) {
            throw new InvalidObjectStateException(
                    "Mission cannot be marked as pending because there are no rockets. Mission: {0}", this
            );
        }
        if (rocketId.length == INVALID_NUMBER_OF_ROCKETS) {
            throw new InvalidObjectStateException(
                    "Mission cannot be marked as pending because number of rockets is invalid. Mission: {0}", this
            );
        }
    }

    private void isTransitionAllowed(MissionStatus current, MissionStatus updated) {
        Set<MissionStatus> allowedTransitions = TRANSITIONS.get(current);
        if (!allowedTransitions.contains(updated)) {
            throw new InvalidObjectStateException(
                    "Mission status {0} cannot be changed to {1}. Available statuses: {2}", current, updated, allowedTransitions
            );
        }
    }

//    private boolean noRocketInRepairing() {
//        for (Map.Entry<RocketId, Rocket> entry: rockets.entrySet()) {
//            if (entry.getValue().getStatus() == RocketStatus.IN_REPAIR) {
//                return false;
//            }
//        }
//        return true;
//    }

    private void validate() {
        if (id == null) {
            throw new InvalidObjectStateException("Mission ID cannot be null. Mission: {0}", this);
        }
        if (name == null || name.isBlank()) {
            throw new InvalidObjectStateException("Mission name cannot be null or blank. Mission: {0}", this);
        }
        if (status == null) {
            throw new InvalidObjectStateException("Mission status cannot be null. Mission: {0}", this);
        }
        if (rockets == null) {
            throw new InvalidObjectStateException("Rockets cannot be null. Mission: {0}", this);
        }
    }

    private Map<RocketId, Rocket> changeRocketsStatus(Function<Rocket, Rocket> changeStatus) {
        return rockets.values()
                .stream()
                .map(changeStatus)
                .collect(Collectors.toUnmodifiableMap(Rocket::getId, r -> r));
    }
}
