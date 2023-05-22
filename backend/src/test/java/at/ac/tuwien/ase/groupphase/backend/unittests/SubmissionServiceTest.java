package at.ac.tuwien.ase.groupphase.backend.unittests;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Challenge;
import at.ac.tuwien.ase.groupphase.backend.entity.League;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import at.ac.tuwien.ase.groupphase.backend.exception.ForbiddenAccessException;
import at.ac.tuwien.ase.groupphase.backend.mapper.SubmissionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.*;
import at.ac.tuwien.ase.groupphase.backend.service.SubmissionService;
import jakarta.transaction.Transactional;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static util.Constants.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SubmissionServiceTest {
    @Autowired
    private SubmissionService submissionService;
    private Challenge ch;
    private Participant p1;
    private Participant p2;
    private Participant p3;
    private Submission s1;
    private Submission s2;

    @BeforeEach
    void beforeEach() {

    }

}
