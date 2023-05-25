package com.palaver.data.generated

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "media")
class MediaData {
    @GeneratedValue
    @Id
    @Column(name = "id")
    var id: UUID? = null

    @Basic
    @Column(name = "name")
    private var name: String? = null

    @Basic
    @Column(name = "location")
    private var location: String? = null

    @Basic
    @Column(name = "type")
    private var type: String? = null

    @ManyToOne
    @JoinColumn(name = "story_id", referencedColumnName = "id")
    var story: StoryData? = null
    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val mediaData = o as MediaData
        if (id != mediaData.id) return false
        if (name != mediaData.name) return false
        if (location != mediaData.location) return false
        return type == mediaData.type
    }

    override fun hashCode(): Int {
        var result = if (id != null) id.hashCode() else 0
        result = 31 * result + if (name != null) name.hashCode() else 0
        result = 31 * result + if (location != null) location.hashCode() else 0
        result = 31 * result + if (type != null) type.hashCode() else 0
        return result
    }
}
