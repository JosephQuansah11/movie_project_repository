package joe.amethyst.backend_tutorials.demo.solid_principles.domain;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    Movie toDomain(MovieDB entity);

    MovieDB toEntity(Movie domain);
}
