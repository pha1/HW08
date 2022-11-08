/**
 * Group 9 HW 08
 * CreateChatFragment.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateChatFragment extends Fragment {

    FragmentCreateChatBinding binding;
    final String TAG = "test";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateChatFragment newInstance(String param1, String param2) {
        CreateChatFragment fragment = new CreateChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.create_chat);

        // Cancel Button
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToMyChats();
            }
        });

        // Submit Button
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Implement starting a message with a user
                String chat = binding.editTextMessage.getText().toString();

                if (chat.isEmpty()){
                    Toast.makeText(getContext(), "Please enter a chat!", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO add Chat to database

                    // Go back to My Chats
                    mListener.goToMyChats();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof CreateChatFragmentListener) {
            mListener = (CreateChatFragmentListener) context;
        }
    }

    CreateChatFragmentListener mListener;

    public interface CreateChatFragmentListener {
        void goToMyChats();
    }
}