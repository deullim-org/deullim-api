package org.deullim.api.domain

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.deullim.api.domain.converter.DevicesConverter

@Entity
@Table(name = "members")
class Member protected constructor() : Base() { // ✅ private -> protected

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: Status = Status.ACTIVE
        protected set

    @Column(name = "nickname", nullable = false)
    var nickname: String = ""
        protected set

    @Column(name = "devices", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = DevicesConverter::class)
    var devices: List<Device> = emptyList()
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    var source: SourceType = SourceType.GUEST
        protected set

    companion object {
        fun create(
            nickname: String,
            device: Device,
            source: SourceType,
        ) = Member().apply {
            require(nickname.isNotBlank()) { "nickname must not be blank" }
            require(device.token.isNotBlank()) { "device.token must not be blank" }
            require(device.os.isNotBlank()) { "device.os must not be blank" }

            this.nickname = nickname
            this.devices = listOf(device)
            this.source = source
        }
    }

    fun activate() {
        require(status == Status.INACTIVE) { "cannot activate active member" }
        status = Status.ACTIVE
    }

    fun deactivate() {
        require(status == Status.ACTIVE) { "cannot deactivate inactive member" }
        status = Status.INACTIVE
    }

    fun delete() {
        require(status != Status.DELETED) { "already deleted member" }
        status = Status.DELETED
    }

    fun changeNickname(nickname: String) {
        require(nickname.isNotBlank()) { "nickname must not be blank" }
        this.nickname = nickname
    }

    fun addDevice(device: Device) {
        require(device.token.isNotBlank()) { "device.token must not be blank" }
        require(device.os.isNotBlank()) { "device.os must not be blank" }
        devices = devices + device
    }

    fun removeDevice(device: Device) {
        devices = devices.filterNot { it.token == device.token && it.os == device.os }
    }
}

data class Device(
    val token: String,
    val os: String,
)

enum class Status { ACTIVE, INACTIVE, DELETED }

enum class SourceType { APPLE, GOOGLE, GUEST }
