package com.example.job3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.job3.databinding.ItemFriendBinding
import com.example.job3.model.AppUser

/**
 * Adapter for the RecyclerView in [com.example.job3.FriendListActivity].
 * Displays a list of [AppUser] entities and handles item click events.
 *
 * @param users The initial list of users to display.
 * @param onItemClick Callback triggered when a user card is tapped.
 */
class FriendAdapter(
    private var users: List<AppUser>,
    private val onItemClick: (AppUser) -> Unit
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    /**
     * ViewHolder class that holds references to the views in the item layout.
     */
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

        // Format user details for display
        val displayName = user.displayName
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "Unknown User"

        val email = user.userEmail
            .trim()
            .ifEmpty { "No email available" }

        holder.binding.tvName.text = displayName
        holder.binding.tvEmail.text = email

        // Setup the click listener for the entire card
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    /**
     * Updates the data set and refreshes the UI.
     * @param newUsers The new list of users to be displayed.
     */
    fun updateList(newUsers: List<AppUser>) {

        users = newUsers

        notifyDataSetChanged()
    }
}