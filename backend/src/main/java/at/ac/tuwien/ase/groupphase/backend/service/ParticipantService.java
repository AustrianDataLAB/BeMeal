package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.mapper.ParticipantMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    public ParticipantDto getParticipantDto() {
        final String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Participant participant = this.participantRepository.findByUsername(username);
        return this.participantMapper.participantToParticipantDto(participant);
    }

}