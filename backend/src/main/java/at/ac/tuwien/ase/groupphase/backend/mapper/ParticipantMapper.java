package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ParticipantMapper {

    @Mapping(source = "participant.isAdmin", target = "admin")
    ParticipantDto participantToParticipantDto(Participant participant);

    Participant participantDtoToParticipant(ParticipantDto participant);

}
