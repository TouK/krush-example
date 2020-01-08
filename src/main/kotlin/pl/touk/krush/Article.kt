package pl.touk.krush

import javax.persistence.*

@Entity
@Table(name = "articles")
data class Article(
    @Id @GeneratedValue
    val id: Long? = null,

    @Column(name = "title")
    val title: String,

    @ManyToMany
    @JoinTable(name = "article_tags")
    val tags: List<Tag> = emptyList()
)

@Entity
@Table(name = "tags")
data class Tag(
    @Id @GeneratedValue
    val id: Long? = null,

    @Column(name = "name")
    val name: String
)
