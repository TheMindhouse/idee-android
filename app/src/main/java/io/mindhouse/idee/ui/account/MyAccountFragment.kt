package io.mindhouse.idee.ui.account

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.mindhouse.idee.R
import io.mindhouse.idee.ui.base.MvvmFragment
import kotlinx.android.synthetic.main.fragment_my_account.*

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
class MyAccountFragment : MvvmFragment<MyAccountViewState, MyAccountViewModel>() {

    private val adapter = BoardsRecyclerAdapter()

    var onBoardSelectedListener: ((String) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_my_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lm = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = lm

        adapter.onItemClickedListener = { boardViewState, _ ->
            onBoardSelectedListener?.invoke(boardViewState.id)
        }

        addBoardButton.setOnClickListener {
            //todo change name!
            viewModel.createNewBoard("Moja tablica bjacz!!")
        }
    }

    override fun render(state: MyAccountViewState) {
        userName.text = state.me?.name ?: getString(R.string.user_no_name)
        if (state.me?.avatarUrl != null) {
            Picasso.get().load(state.me.avatarUrl).into(avatar)
        }

        adapter.setItems(state.boards)
    }

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[MyAccountViewModel::class.java]

}