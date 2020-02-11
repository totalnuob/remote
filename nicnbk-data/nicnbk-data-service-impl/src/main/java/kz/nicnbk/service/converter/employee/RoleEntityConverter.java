package kz.nicnbk.service.converter.employee;

import kz.nicnbk.repo.model.employee.Role;
import kz.nicnbk.service.converter.dozer.BaseDozerTypedEntityConverter;
import kz.nicnbk.service.dto.employee.RoleDto;
import org.springframework.stereotype.Component;

/**
 * Created by pak on 05.02.2020.
 */
@Component
public class RoleEntityConverter extends BaseDozerTypedEntityConverter<Role, RoleDto> {
}
