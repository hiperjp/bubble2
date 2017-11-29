add_library( # Specifies the name of the library.
             bitmap-styler

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/bitmap-styler.cpp )

target_link_libraries(bitmap-styler jnigraphics log)