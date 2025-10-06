package user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import error.ConflictException;
import error.NotFoundException;
import user.dto.NewUserRequest;
import user.dto.UserDto;
import user.mapper.UserMapper;
import user.repo.UserRepository;
import util.PageRequestUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements user.service.UserService {
    private final UserRepository repo;

    @Override @Transactional
    public UserDto create(NewUserRequest req) {
        if (repo.existsByEmailIgnoreCase(req.getEmail()))
            throw new ConflictException("User with email already exists: " + req.getEmail());
        return UserMapper.toDto(repo.save(UserMapper.toEntity(req)));
    }

    @Override
    public List<UserDto> get(List<Long> ids, int from, int size) {
        Pageable pr = PageRequestUtil.of(from, size, Sort.by("id").ascending());
        var page = (ids == null || ids.isEmpty())
                ? repo.findAll(pr)
                : repo.findAllByIdIn(ids, pr);
        return page.map(UserMapper::toDto).getContent();
    }

    @Override
    @Transactional
    public void delete(long userId) {
        if (!repo.existsById(userId)) throw new NotFoundException("User with id=" + userId + " was not found");
        repo.deleteById(userId);
    }
}

