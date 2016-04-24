package example1.mothership.core;

import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.control.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.Objects;

import static example1.mothership.core.MothershipDataSchema.*;
import static example1.mothership.core.MothershipDataSchema.MothershipStatus.AVALIABLE;
import static example1.mothership.core.MothershipDataSchema.MothershipStatus.ON_MISSION;

@Value
@AllArgsConstructor
@Wither
@Builder
public class MothershipAggregateRoot {

    MothershipId id;
    Set<Rover> rovers;

    // mutable data
    MothershipStatus status;
    Option<Mission> mission;

    // service
    transient TemperatureService temperatureService;

    // events emitters

    public List<? super MothershipEvent> create(MothershipId id, Set<Rover> avaliableRovers) {
        isNew();
        hasAtLeastOneRover(avaliableRovers.size());
        return List.of(new MothershipCreated(id, avaliableRovers));
    }

    public List<? super MothershipEvent> startMission(MissionId missionId, Plateau plateau) {
        isNotNew();
        isFirstMission();
        statusIs(AVALIABLE);
        return List.of(new MissionStarted(new Mission(missionId, plateau)));
    }

    public List<? super MothershipEvent> landRover(RoverId roverId, RoverPosition roverPosition) {
        isNotNew();
        statusIs(ON_MISSION);
        hasRover(roverId);
        mission.get().canLaunchRover(roverId, roverPosition, temperatureService);
        return List.of(new RoverLaunched(roverId, roverPosition));
    }

    private void hasRover(RoverId roverId) {
        if (rovers.filter(r -> r.getId().equals(roverId)).size()==0){
            throw new IllegalStateException(String.format("this mothership does not have this rover: %s", roverId));
        }
    }

    // guards

    void statusIs(MothershipStatus requiredStatus) {
        if (!requiredStatus.equals(status)) {
            throw new IllegalStateException(String.format("status must be = %s", requiredStatus));
        }
    }

    void isNew() {
        Objects.isNull(id);
    }

    void hasAtLeastOneRover(int howManyRovers) {
         if (howManyRovers == 0) {
             throw new IllegalStateException("you can't start a mission without any rover");
         }
    }

    void isNotNew() {
        Objects.requireNonNull(id);
    }

    void isFirstMission() {
        if (mission.isDefined()) {
            throw new IllegalStateException("this mothership can have only 1 mission");
        }
    }

}
