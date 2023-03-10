package core.vfs

import core.user.User
import core.user.isSugoi
import core.utils.getFlag
import core.vfs.Permission.Companion.Operation
import kotlinx.serialization.Serializable
import kotlin.Boolean

enum class PermissionTarget {
    OtherX,
    OtherW,
    OtherR,
    GroupX,
    GroupW,
    GroupR,
    OwnerX,
    OwnerW,
    OwnerR
}

@Serializable
/**
 * @param value 0~511 (OCT:777)(BIN:111_111_111)までの範囲
 */
@JvmInline
value class Permission(val value: Int) {

    constructor(vararg targets: PermissionTarget) : this(targets.fold(0) { acc, permissionTarget ->
        acc or permissionTarget.getFlag()
    })

    companion object {
        private val permissionLabel = listOf("x", "w", "r")

        //rw-rw-r--
        val fileDefault = Permission(0b110_110_100)
        //rwxrwxr-x
        val exeDefault = fileDefault + PermissionTarget.OwnerX + PermissionTarget.GroupX + PermissionTarget.OtherX

        //rwxrwxr-x
        val dirDefault = Permission(0b111_111_101)

        enum class Operation{
            Execute,
            Write,
            Read
        }

    }

    operator fun plus(target: PermissionTarget): Permission {
        return Permission(value or target.getFlag())
    }

    /**
     * 有効な[PermissionTarget]を取得します
     * @return [PermissionTarget]の[List]を返します。
     * */
    fun getPermissions(): List<PermissionTarget> {
        return PermissionTarget.values().filter {
            has(it)
        }
    }

    /**
     * 対象の権限があるかを確認します。
     * @param permission 対象の権限
     */
    fun has(permission: PermissionTarget): Boolean = value and permission.getFlag() != 0
    fun has(permissionValue: Int): Boolean = value and permissionValue != 0

    override fun toString(): String {
        var str = ""
        repeat(9) {
            str = (if (value and (1 shl it) != 0) {
                permissionLabel[(it) % 3]
            } else "-") + str
        }
        return str
    }

}
private fun File.getOperatorValue(user: User) = when {
    owner.get() == user -> 6
    ownerGroup.get() == user.group -> 3
    else -> 0
}

fun File.checkPermission(user: User,operation: Operation): Boolean =
    isSugoi(user) || permission.get().has(operation.getFlag() shl getOperatorValue(user))
