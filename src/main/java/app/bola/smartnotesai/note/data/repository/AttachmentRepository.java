package app.bola.smartnotesai.note.data.repository;

import app.bola.smartnotesai.note.data.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}