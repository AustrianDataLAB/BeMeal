package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.mapper.ParticipantMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    @Transactional("rdbmsTxManager")
    public ParticipantDto getParticipantDto() {
        final String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Participant participant = this.participantRepository.findByUsername(username);
        return this.participantMapper.participantToParticipantDto(participant);
    }

    @Transactional("rdbmsTxManager")
    public void increaseWinsOfParticipant(Long id, Long leagueId) {
        Participant participant = participantRepository.findById(id).orElseThrow();

        participant.getWins().putIfAbsent(leagueId, 0);
        participant.getWins().put(leagueId, participant.getWins().get(leagueId) + 1);
        // participant.setWins(participant.getWins() + 1);
    }

}
