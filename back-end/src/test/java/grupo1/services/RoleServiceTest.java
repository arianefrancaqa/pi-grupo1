package grupo1.services;

import grupo1.dtos.RoleDTO;
import grupo1.entities.Role;
import grupo1.exceptions.NotFoundException;
import grupo1.repositories.IRoleRepository;
import grupo1.repositories.IUserRepository;
import grupo1.services.impl.RoleServiceImpl;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Log4j
@ExtendWith(SpringExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private IUserRepository userRepository;

    private Role existingRole;

    @BeforeEach
    void setUp() {
        existingRole = new Role();
        existingRole.setId(1);
        existingRole.setNome("ROLE_EXISTING");
        roleRepository.save(existingRole);

        when(roleRepository.findById(1)).thenReturn(Optional.of(existingRole));
        when(roleRepository.findById(2)).thenReturn(Optional.empty());
    }

    @Test
    void testSaveNewRole() {
        log.info("TEST START - SAVE NEW ROLE");
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setNome("ROLE_123");
        roleDTO.setId(2);
        var savedRoleDTO = roleService.save(roleDTO);

        assertNotNull(savedRoleDTO);
        log.info("TEST FINISH - SAVE NEW ROLE ");
    }

    @Test
    void testFindAllRoles() {
        log.info("TEST START - FIND ALL ROLES");
        Page<Role> rolePage = new PageImpl<>(List.of(existingRole));
        Pageable pageable = Pageable.unpaged();

        when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        Page<RoleDTO> resultPage = roleService.findAll(pageable);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(existingRole.getNome(), resultPage.getContent().get(0).getNome());
        log.info("TEST FINISH - FIND ALL ROLES");
    }

    @Test
    void testFindByIdExisting() {
        log.info("TEST START - FIND ROLE BY EXISTING ID");
        Optional<RoleDTO> result = roleService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(existingRole.getNome(), result.get().getNome());
        log.info("TEST START - FIND ROLE BY EXISTING ID");
    }

    @Test
    void testFindByIdNonExisting() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO FIND ROLE BY NON-EXISTENT ID");
        assertThrows(NotFoundException.class, () -> roleService.findById(2));
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO FIND ROLE BY NON-EXISTENT ID");
    }

    @Test
    void testUpdateExistingRole() {
        log.info("TEST START - UPDATE ROLE");
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setNome("ROLE_UPDATED");

        when(roleRepository.save(any(Role.class))).thenReturn(existingRole);
        RoleDTO updatedRoleDTO = roleService.update(1, roleDTO);

        assertEquals(roleDTO.getNome(), updatedRoleDTO.getNome());
        log.info("TEST FINISH - UPDATE ROLE");
    }

    @Test
    void testUpdateNonExistingRole() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO UPDATE NON-EXISTENT ROLE");
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setNome("ROLE_UPDATED");

        assertThrows(NotFoundException.class, () -> roleService.update(2, roleDTO));
        log.info("TEST END - THROW EXCPETION WHEN TRYING TO UPDATE NON-EXISTENT ROLE");
    }

    @Test
    void testDeleteExistingRole() {
        log.info("TEST START - DELETE ROLE");
        assertDoesNotThrow(() -> roleService.delete(1));
        log.info("TEST FINISH - DELETE ROLE");
    }

    @Test
    void testDeleteNonExistingRole() {
        log.info("TEST START - THROW EXCPETION WHEN TRYING TO DELETE NON-EXISTENT ROLE");
        assertThrows(NotFoundException.class, () -> roleService.delete(2));
        log.info("TEST FINISH - THROW EXCPETION WHEN TRYING TO DELETE NON-EXISTENT ROLE");
    }
}
