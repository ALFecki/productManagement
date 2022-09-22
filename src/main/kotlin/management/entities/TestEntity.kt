package management.entities

import io.micronaut.data.annotation.GeneratedValue

import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "test", schema = "public")
data class TestEntity(

    @Id val id: Int,

    @Column(name = "name")
    val name : String
)
