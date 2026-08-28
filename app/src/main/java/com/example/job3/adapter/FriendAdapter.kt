package com.example.job3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.job3.databinding.ItemFriendBinding
import com.example.job3.model.AppUser

class FriendAdapter(
    private var users: List<AppUser>,
    private val onItemClick: (AppUser) -> Unit
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    class FriendViewHolder(
        val binding: ItemFriendBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendViewHolder {

        val binding = ItemFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FriendViewHolder,
        position: Int
    ) {

        val user = users[position]

        val displayName = user.displayName
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "Unknown User"

        val email = user.userEmail
            .trim()
            .ifEmpty { "No email available" }

        holder.binding.tvName.text = displayName
        holder.binding.tvEmail.text = email

        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateList(newUsers: List<AppUser>) {

        users = newUsers

        notifyDataSetChanged()
    }
}