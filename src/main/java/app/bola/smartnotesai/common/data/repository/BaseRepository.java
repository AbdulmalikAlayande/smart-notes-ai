package app.bola.smartnotesai.common.data.repository;

import app.bola.smartnotesai.common.data.model.BaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseModel> extends JpaRepository<T, String> {

    Optional<T> findByPublicId(String publicId);
}
