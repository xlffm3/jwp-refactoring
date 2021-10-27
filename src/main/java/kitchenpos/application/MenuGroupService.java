package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.application.dto.request.menu.MenuGroupRequestDto;
import kitchenpos.application.dto.response.menu.MenuGroupResponseDto;
import kitchenpos.domain.repository.MenuGroupRepository;
import kitchenpos.domain.MenuGroup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class MenuGroupService {

    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupService(MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    @Transactional
    public MenuGroupResponseDto create(final MenuGroupRequestDto menuGroupRequestDto) {
        MenuGroup menuGroup = new MenuGroup(menuGroupRequestDto.getName());
        return MenuGroupResponseDto.from(menuGroupRepository.save(menuGroup));
    }

    public List<MenuGroupResponseDto> list() {
        return menuGroupRepository.findAll()
            .stream()
            .map(MenuGroupResponseDto::from)
            .collect(Collectors.toList());
    }
}
