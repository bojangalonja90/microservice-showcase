package com.bojangalonja.bookshelves.mapper;

import com.bojangalonja.bookshelves.model.Bookshelf;
import com.bojangalonja.bookshelves.model.BookshelfDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookshelfMapper {

    BookshelfDTO bookshelfToBookshelfDto(Bookshelf bookshelf);

    Bookshelf bookshelfDtoToBookshelf(BookshelfDTO bookshelfDto);

}
