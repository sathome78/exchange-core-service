package me.exrates.model.main;

import java.nio.file.Path;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class UserFile {

    private int id;
    private int userId;
    private Path path;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(final Path path) {
        this.path = path;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserFile userFile = (UserFile) o;

        if (id != userFile.id) return false;
        if (userId != userFile.userId) return false;
        return path != null ? path.equals(userFile.path) : userFile.path == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserFile{" +
                "id=" + id +
                ", userId=" + userId +
                ", path=" + path +
                '}';
    }
}
