package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.game.prep.JoinGameActivity
import com.github.fribourgsdp.radio.data.User
import org.mockito.Mockito.*

class WorkingJoinGameActivity : JoinGameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context : Context = mock(Context::class.java)
        User.setFSGetter(MockFileSystem.MockFSGetter)
        User("The second best player").save(context)
    }

    override fun initDatabase(): Database {
        return LocalDatabase()
    }
}

class BuggyJoinGameActivity : JoinGameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context : Context = mock(Context::class.java)
        User.setFSGetter(MockFileSystem.MockFSGetter)
        User("The buggy player").save(context)
    }

    override fun initDatabase(): Database {
        return BuggyDatabase()
    }
}


class QRCodeJoinGameActivity : JoinGameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context : Context = mock(Context::class.java)
        User.setFSGetter(MockFileSystem.MockFSGetter)
        User("The second best player").save(context)
    }

    override fun initDatabase(): Database {
        return LocalDatabase()
    }

    override fun createQRCodeFragment() : DialogFragment {
        return MockJoinWithQRCodeFragment(this, this)
    }
}

class QRCodeJoinGameActivityJoin : JoinGameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context : Context = mock(Context::class.java)
        User.setFSGetter(MockFileSystem.MockFSGetter)
        User("The second best player").save(context)
    }

    override fun initDatabase(): Database {
        return LocalDatabase()
    }

    override fun createQRCodeFragment() : DialogFragment {
        val mock = MockJoinWithQRCodeFragment(this, this)
        mock.setJoin()
        return mock
    }
}

class QRCodeJoinGameActivityJoinFail : JoinGameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context : Context = mock(Context::class.java)
        User.setFSGetter(MockFileSystem.MockFSGetter)
        User("The second best player").save(context)
    }

    override fun initDatabase(): Database {
        return LocalDatabase()
    }

    override fun createQRCodeFragment() : DialogFragment {
        val mock = MockJoinWithQRCodeFragment(this, this)
        mock.setJoin()
        mock.setData("fault")
        return mock
    }
}
