package dev.kachvev.serverscript.telegram.enum

data object Buttons {
    data object StartMenu {
        const val PROFILE = "profile"
        const val MARKETPLACE = "marketplace"
        const val LINK_ACCOUNT = "add_account"
        const val UNLINK_ACCOUNT = "no_account"
    }

    data object Marketplace {
        const val UPLOAD = "upload_script"
        const val PAGE = "page_script"
        const val ADD_SCRIPT = "add_script"
    }

    data object AdminMenu {
        const val STATS = "admin_stats"
        const val DELETE_SCRIPT = "admin_delete_script"
    }
}
