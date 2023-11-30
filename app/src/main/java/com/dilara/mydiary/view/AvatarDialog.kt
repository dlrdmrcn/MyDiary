package com.dilara.mydiary.view

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import com.dilara.mydiary.adapter.AvatarRecyclerAdapter
import com.dilara.mydiary.databinding.AvatarDialogBinding

class AvatarDialog(
    context: Context,
    var avatarList: ArrayList<Int>,
    var imageButton: ImageButton
) :
    Dialog(context), AvatarRecyclerAdapter.Listener {
    private lateinit var binding: AvatarDialogBinding
    private lateinit var avatarAdapter: AvatarRecyclerAdapter
    private lateinit var sharedPref: SharedPreferences

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = AvatarDialogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPref = context.getSharedPreferences("com.dilara.mydiary.view", Context.MODE_PRIVATE)

        avatarAdapter = AvatarRecyclerAdapter(avatarList, context, this@AvatarDialog)
        binding.avatarRecyclerView.layoutManager = GridLayoutManager(context, 5)
        binding.avatarRecyclerView.adapter = avatarAdapter
    }

    override fun onAvatarClick(position: Int) {
        imageButton.setImageResource(position)
        sharedPref.edit().putInt("avatar", position).apply()
        this.dismiss()
    }
}